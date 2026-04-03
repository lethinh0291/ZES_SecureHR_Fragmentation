# ZES-SecureHR - Hệ thống quản lý nhân sự và bảng lương

Ứng dụng desktop quản lý nhân sự hiện đại với giao diện SaaS-style, dữ liệu phân mảnh theo vai trò, và quản lý bảo mật lương thưởng.

## 🎯 Tính năng chính

### Kiến trúc dữ liệu

- **Phân mảnh dọc (Vertical Fragmentation)**: Dữ liệu nhạy cảm tách riêng
  - `SecureHR_DB1`: Hồ sơ nhân viên & tài khoản đăng nhập
  - `SecureHR_DB2`: Dữ liệu lương & thưởng
- **Đồng bộ dữ liệu**: Insert/Delete được đồng bộ qua stored procedures
- **Phân quyền theo vai trò**: 3 loại tài khoản (ADMIN, HR_USER, ACCOUNTANT)

### Giao diện người dùng

- **Thiết kế SaaS hiện đại**: FlatLaf L&F + glassmorphism effects
- **Navigation linh hoạt**: Sidebar với các trang quản lý khác nhau
- **Header đầy đủ**: Breadcrumb, title, status badge, user chip
- **Responsive layout**: Bảng dữ liệu, search, filter, action buttons
- **Vietnamese localization**: Toàn bộ UI sử dụng tiếng Việt

## 📱 Các trang ứng dụng

### Trang Admin (ADMIN)

1. **Bảng điều khiển** - Dashboard chính với KPI metrics
2. **Quản lý nhân viên** - Danh sách, thêm, xóa nhân viên
3. **Quản lý lương** - Xem/quản lý lương, phụ cấp, thưởng
4. **Báo cáo** - Báo cáo phân tích chi tiết

### Trang Người dùng (HR/Accountant)

1. **Tổng quan** - Dashboard tổng hợp dữ liệu được cấp
2. **Dữ liệu được cấp** - Xem dữ liệu phù hợp vai trò
3. **Thống kê** - Báo cáo thống kê cơ bản

## 🗂️ Cấu trúc dự án

```
src/
├── App.java                           # Entry point
├── securehr/
│   ├── config/DatabaseConfig.java     # SQL Server connection settings
│   ├── db/DatabaseManager.java        # Connection pool
│   ├── model/AppUser.java            # User model
│   ├── service/
│   │   ├── AuthService.java          # Authentication
│   │   ├── EmployeeService.java      # Employee operations
│   │   └── SalaryService.java        # Salary operations (implied)
│   └── ui/
│       ├── LoginFrame.java           # Login screen
│       ├── AdminFrame.java           # Admin dashboard
│       ├── EmployeeFrame.java        # Employee management
│       ├── SalaryFrame.java          # Salary management
│       ├── ReportFrame.java          # Reports
│       ├── UserFrame.java            # User dashboard
│       ├── DataAccessFrame.java      # User data access
│       ├── StatisticsFrame.java      # User statistics
│       ├── UiTheme.java              # Centralized styling & components
│       ├── GlassPanel.java           # Glass effect panel
│       └── GradientBackgroundPanel.java  # Gradient background

sql/
└── setup_securehr.sql                 # Database initialization script
```

## 🚀 Yêu cầu hệ thống

- **Java**: 17 hoặc mới hơn
- **SQL Server**: 2019 hoặc mới hơn (local hoặc remote)
- **JDBC Driver**: `mssql-jdbc-12.6.1.jre11.jar` (hoặc phiên bản compatible)

## 🔧 Cài đặt cơ sở dữ liệu

1. Mở **SQL Server Management Studio**
2. Thực thi script: `sql/setup_securehr.sql`
3. Script sẽ tạo:
   - 2 databases (DB1 & DB2)
   - Tables, Views, Indexes
   - Stored Procedures cho CRUD operations
   - 3 demo user accounts
   - Sample data (nhân viên, lương)

## 👤 Tài khoản demo

| Tên đăng nhập | Mật khẩu | Vai trò    | Quyền truy cập              |
| ------------- | -------- | ---------- | --------------------------- |
| admin         | admin123 | ADMIN      | Toàn bộ dữ liệu             |
| hr            | hr123    | HR_USER    | Dữ liệu nhân viên công khai |
| accountant    | acc123   | ACCOUNTANT | Dữ liệu lương bảo mật       |

## ⚙️ Cấu hình kết nối

Chỉnh sửa file `src/securehr/config/DatabaseConfig.java`:

```java
public class DatabaseConfig {
    public static final String SERVER = "localhost";        // Server SQL
    public static final int PORT = 1433;                    // Port
    public static final String DB_USER = "sa";             // Username
    public static final String DB_PASSWORD = "password";   // Password
}
```

## 🏃 Chạy ứng dụng

### Option 1: Từ VS Code

1. Mở `src/App.java`
2. Click "Run"

### Option 2: Từ Terminal

```bash
# Compile (nếu chưa có)
javac -cp "lib/*" -d bin $(find src -name "*.java")

# Run
java -cp "bin;lib/*" App
```

## 📊 Quy trình quản lý dữ liệu

### Admin - Xem đầy đủ

```
Admin → Query through sp_AdminGetEmployees → DB1 + DB2 →
  ├── Employee info (DB1)
  ├── Profile data (DB1)
  ├── Salary info (DB2)
  └── Combined result set
```

### User - Xem giới hạn (HR_USER)

```
HR_USER → Query public data → DB1 only →
  ├── MaNV, HoTen, PhongBan, ChucVu, Email
  └── No salary or sensitive data
```

### User - Xem giới hạn (ACCOUNTANT)

```
ACCOUNTANT → Query salary data → DB2 only →
  ├── Mã NV, Tên NV
  ├── Lương cơ bản, Hệ số, Phụ cấp, Thưởng
  └── No personal profile details
```

## 🔒 Bảo mật

- **Role-based Access Control (RBAC)**: Mỗi vai trò chỉ được truy cập dữ liệu phù hợp
- **SQL Injection Prevention**: Sử dụng PreparedStatements
- **Data Fragmentation**: Lương được lưu riêng biệt trên DB2
- **Audit Trail**: Các tác vụ thêm/xóa được tracked qua stored procedures

⚠️ **Lưu ý bảo mật**: Password hiện được lưu dạng plain text cho mục đích demo.
Trong production, sử dụng hashing algorithm (BCrypt/Argon2) + salt.

## 💡 Các công nghệ sử dụng

- **UI Framework**: Java Swing + FlatLaf 3.7
- **Database**: SQL Server 2019+ với JDBC
- **Build**: Java compiler (javac)
- **Connection Pool**: DatabaseManager (manual pooling)
- **Localization**: Vietnamese (vi_VN)
