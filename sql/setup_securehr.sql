/*
Run this script in SQL Server Management Studio using a sysadmin account.
It creates:
- DB1 (public profile + app users)
- DB2 (salary)
- View + Stored Procedures for transparent admin operations
- Demo app users and sample data
*/

IF DB_ID('SecureHR_DB1') IS NULL
BEGIN
    CREATE DATABASE SecureHR_DB1;
END
GO

IF DB_ID('SecureHR_DB2') IS NULL
BEGIN
    CREATE DATABASE SecureHR_DB2;
END
GO

USE SecureHR_DB1;
GO

IF OBJECT_ID('dbo.Employees', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.Employees (
        MaNV NVARCHAR(20) NOT NULL PRIMARY KEY,
        HoTen NVARCHAR(100) NOT NULL,
        PhongBan NVARCHAR(100) NOT NULL,
        ChucVu NVARCHAR(100) NOT NULL,
        Email NVARCHAR(150) NOT NULL UNIQUE,
        CreatedAt DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME()
    );
END
GO

IF OBJECT_ID('dbo.AppUsers', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.AppUsers (
        Username NVARCHAR(50) NOT NULL PRIMARY KEY,
        PasswordHash NVARCHAR(255) NOT NULL,
        FullName NVARCHAR(120) NOT NULL,
        RoleName NVARCHAR(20) NOT NULL,
        IsActive BIT NOT NULL DEFAULT 1,
        CONSTRAINT CK_AppUsers_RoleName CHECK (RoleName IN ('ADMIN', 'HR_USER', 'ACCOUNTANT'))
    );
END
GO

USE SecureHR_DB2;
GO

IF OBJECT_ID('dbo.Salaries', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.Salaries (
        MaNV NVARCHAR(20) NOT NULL PRIMARY KEY,
        LuongCoBan DECIMAL(18, 2) NOT NULL,
        HeSo DECIMAL(10, 2) NOT NULL,
        PhuCap DECIMAL(18, 2) NOT NULL DEFAULT 0,
        Thuong DECIMAL(18, 2) NOT NULL DEFAULT 0,
        UpdatedAt DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME()
    );
END
GO

USE SecureHR_DB1;
GO

CREATE OR ALTER VIEW dbo.v_AdminEmployeeFull
AS
SELECT
    e.MaNV,
    e.HoTen,
    e.PhongBan,
    e.ChucVu,
    e.Email,
    s.LuongCoBan,
    s.HeSo,
    s.PhuCap,
    s.Thuong,
    (s.LuongCoBan * s.HeSo + s.PhuCap + s.Thuong) AS TongLuong,
    e.CreatedAt
FROM dbo.Employees e
LEFT JOIN SecureHR_DB2.dbo.Salaries s
    ON e.MaNV = s.MaNV;
GO

CREATE OR ALTER PROCEDURE dbo.sp_AdminGetEmployees
AS
BEGIN
    SET NOCOUNT ON;
    SELECT *
    FROM dbo.v_AdminEmployeeFull
    ORDER BY MaNV;
END
GO

CREATE OR ALTER PROCEDURE dbo.sp_AdminInsertEmployeeWithSalary
    @MaNV NVARCHAR(20),
    @HoTen NVARCHAR(100),
    @PhongBan NVARCHAR(100),
    @ChucVu NVARCHAR(100),
    @Email NVARCHAR(150),
    @LuongCoBan DECIMAL(18, 2),
    @HeSo DECIMAL(10, 2),
    @PhuCap DECIMAL(18, 2),
    @Thuong DECIMAL(18, 2)
AS
BEGIN
    SET NOCOUNT ON;

    BEGIN TRY
        BEGIN TRAN;

        INSERT INTO SecureHR_DB1.dbo.Employees (MaNV, HoTen, PhongBan, ChucVu, Email)
        VALUES (@MaNV, @HoTen, @PhongBan, @ChucVu, @Email);

        INSERT INTO SecureHR_DB2.dbo.Salaries (MaNV, LuongCoBan, HeSo, PhuCap, Thuong)
        VALUES (@MaNV, @LuongCoBan, @HeSo, @PhuCap, @Thuong);

        COMMIT TRAN;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0
            ROLLBACK TRAN;

        THROW;
    END CATCH
END
GO

CREATE OR ALTER PROCEDURE dbo.sp_AdminDeleteEmployee
    @MaNV NVARCHAR(20)
AS
BEGIN
    SET NOCOUNT ON;

    BEGIN TRY
        BEGIN TRAN;

        DELETE FROM SecureHR_DB2.dbo.Salaries WHERE MaNV = @MaNV;
        DELETE FROM SecureHR_DB1.dbo.Employees WHERE MaNV = @MaNV;

        COMMIT TRAN;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0
            ROLLBACK TRAN;

        THROW;
    END CATCH
END
GO

-- Seed users (demo password is plain text for demo only).
MERGE dbo.AppUsers AS target
USING (SELECT 'admin' AS Username, 'admin123' AS PasswordHash, 'System Admin' AS FullName, 'ADMIN' AS RoleName, CAST(1 AS BIT) AS IsActive) AS src
ON target.Username = src.Username
WHEN MATCHED THEN
    UPDATE SET PasswordHash = src.PasswordHash, FullName = src.FullName, RoleName = src.RoleName, IsActive = src.IsActive
WHEN NOT MATCHED THEN
    INSERT (Username, PasswordHash, FullName, RoleName, IsActive)
    VALUES (src.Username, src.PasswordHash, src.FullName, src.RoleName, src.IsActive);
GO

MERGE dbo.AppUsers AS target
USING (SELECT 'hr' AS Username, 'hr123' AS PasswordHash, 'HR Staff' AS FullName, 'HR_USER' AS RoleName, CAST(1 AS BIT) AS IsActive) AS src
ON target.Username = src.Username
WHEN MATCHED THEN
    UPDATE SET PasswordHash = src.PasswordHash, FullName = src.FullName, RoleName = src.RoleName, IsActive = src.IsActive
WHEN NOT MATCHED THEN
    INSERT (Username, PasswordHash, FullName, RoleName, IsActive)
    VALUES (src.Username, src.PasswordHash, src.FullName, src.RoleName, src.IsActive);
GO

MERGE dbo.AppUsers AS target
USING (SELECT 'accountant' AS Username, 'acc123' AS PasswordHash, 'Accounting Staff' AS FullName, 'ACCOUNTANT' AS RoleName, CAST(1 AS BIT) AS IsActive) AS src
ON target.Username = src.Username
WHEN MATCHED THEN
    UPDATE SET PasswordHash = src.PasswordHash, FullName = src.FullName, RoleName = src.RoleName, IsActive = src.IsActive
WHEN NOT MATCHED THEN
    INSERT (Username, PasswordHash, FullName, RoleName, IsActive)
    VALUES (src.Username, src.PasswordHash, src.FullName, src.RoleName, src.IsActive);
GO

IF NOT EXISTS (SELECT 1 FROM SecureHR_DB1.dbo.Employees WHERE MaNV = 'NV001')
BEGIN
    EXEC dbo.sp_AdminInsertEmployeeWithSalary
        @MaNV = 'NV001',
        @HoTen = 'Nguyen Van A',
        @PhongBan = 'HR',
        @ChucVu = 'Specialist',
        @Email = 'nva@securehr.local',
        @LuongCoBan = 12000000,
        @HeSo = 1.2,
        @PhuCap = 500000,
        @Thuong = 1000000;
END
GO

IF NOT EXISTS (SELECT 1 FROM SecureHR_DB1.dbo.Employees WHERE MaNV = 'NV002')
BEGIN
    EXEC dbo.sp_AdminInsertEmployeeWithSalary
        @MaNV = 'NV002',
        @HoTen = 'Tran Thi B',
        @PhongBan = 'Finance',
        @ChucVu = 'Accountant',
        @Email = 'ttb@securehr.local',
        @LuongCoBan = 15000000,
        @HeSo = 1.1,
        @PhuCap = 700000,
        @Thuong = 500000;
END
GO

/*
Optional: SQL Server login-level roles for DB isolation demo.
Uncomment and execute if you want DB-level permission proof.

USE master;
GO
IF NOT EXISTS (SELECT 1 FROM sys.server_principals WHERE name = 'hr_login')
    CREATE LOGIN hr_login WITH PASSWORD = 'Hr@123456';
IF NOT EXISTS (SELECT 1 FROM sys.server_principals WHERE name = 'acc_login')
    CREATE LOGIN acc_login WITH PASSWORD = 'Acc@123456';
GO

USE SecureHR_DB1;
GO
IF NOT EXISTS (SELECT 1 FROM sys.database_principals WHERE name = 'hr_login')
    CREATE USER hr_login FOR LOGIN hr_login;
GRANT SELECT ON dbo.Employees TO hr_login;
DENY SELECT ON dbo.AppUsers TO hr_login;
GO

USE SecureHR_DB2;
GO
IF NOT EXISTS (SELECT 1 FROM sys.database_principals WHERE name = 'acc_login')
    CREATE USER acc_login FOR LOGIN acc_login;
GRANT SELECT ON dbo.Salaries TO acc_login;
GO
*/
