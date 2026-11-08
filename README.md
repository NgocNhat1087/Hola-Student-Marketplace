# Hola Student Marketplace

Bản triển khai Spring Boot + React dựa trên SRS của HSM. Backend nằm trong [backend](./backend), frontend React nằm trong [frontend](./frontend), và thiết kế MySQL được cung cấp nằm trong [HSM_Database_Design.sql](./HSM_Database_Design.sql).

## Chạy cục bộ

1. Sao chép `.env.example` thành `.env` và cấu hình các giá trị MySQL, email và Google khi cần. Nếu không có MySQL, các bài kiểm thử backend sẽ sử dụng H2.
2. Chạy `mvn spring-boot:run` trong thư mục `backend`.
3. Chạy `npm install` và `npm run dev` trong thư mục `frontend`.

Các luồng đã triển khai gồm UC.1 đăng ký và xác minh/gửi lại OTP, UC.2 đăng nhập bằng mật khẩu cùng với lớp tích hợp Google, UC.3 đăng xuất, UC.5 đổi mật khẩu, UC.6 quản lý thông tin văn bản/hình ảnh hồ sơ và UC.7 xem hồ sơ. Các use case khác được cố ý giữ ở mức skeleton.

## UC.5 — Thay đổi mật khẩu

- **Tác nhân:** Người mua, Người bán.
- **Tiền điều kiện:** Người dùng đã đăng nhập và tài khoản có mật khẩu trước đó. Tài khoản tạo bằng Google không dùng được chức năng này; hãy dùng Khôi phục mật khẩu.
- **API:** `POST /api/auth/change-password` với Bearer token và JSON `{ "currentPassword": "...", "newPassword": "...", "confirmPassword": "..." }`.
- **Quy tắc:** Mật khẩu mới tối thiểu 8 ký tự, phải có chữ cái và chữ số, không được trùng mật khẩu cũ và phải khớp phần xác nhận.
- **Kết quả:** Mật khẩu mới được băm bằng BCrypt rồi lưu vào `password_hash`.
- **Lỗi:** `CURRENT_PASSWORD_INCORRECT`, `PASSWORD_UNCHANGED`, `PASSWORD_CONFIRMATION_MISMATCH`, `PASSWORD_NOT_AVAILABLE`.
- **Giao diện:** `/change-password` (chỉ truy cập sau khi đăng nhập).

## Kiểm thử và xác minh

- Backend: chạy `mvn test` trong thư mục `backend`.
- Frontend: cài đặt các dependency rồi chạy `npm run build` trong thư mục `frontend`.

SRS chưa định nghĩa công thức xếp hạng người bán. Điểm đánh giá trung bình chỉ được tính từ các bản ghi đánh giá của giao dịch đã hoàn tất; thứ hạng người bán vẫn là `null` cho đến khi quy tắc nghiệp vụ được xác định. Backend đã được xác minh bằng `mvn test -q` (1 bài kiểm thử đạt). Quá trình cài đặt npm cho frontend bị treo trong môi trường này, nên chưa thể xác nhận kết quả build frontend.
