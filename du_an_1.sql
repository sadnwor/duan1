CREATE DATABASE DuAn1;
GO

USE DuAn1;
GO

CREATE TABLE TaiKhoan (
	id INT IDENTITY(1, 1) PRIMARY KEY,
	tai_khoan VARCHAR(25) NOT NULL,
	mat_khau NVARCHAR(50) NOT NULL,
	email VARCHAR(250) NOT NULL,
	ho_ten NVARCHAR(250),
	sdt VARCHAR(13) NOT NULL,
	gioi_tinh BIT,
	dia_chi NVARCHAR(2000),
	hinh_anh VARCHAR(250),
	otp VARCHAR(6),
	adm BIT DEFAULT 0,
	trang_thai BIT DEFAULT 0,
	ngay_tao DATE NOT NULL DEFAULT GETDATE(),
	trang_thai_xoa BIT DEFAULT 0
);

CREATE TABLE KhachHang (
	id INT IDENTITY(1, 1) PRIMARY KEY,
	ho_ten NVARCHAR(250) NOT NULL,
	sdt VARCHAR(13) NOT NULL,
	gioi_tinh BIT,
	dia_chi NVARCHAR(2000),
	ngay_tao DATETIME NOT NULL DEFAULT GETDATE(),
	trang_thai_xoa BIT DEFAULT 0
);


CREATE TABLE DanhMuc (
	id INT IDENTITY(1, 1) PRIMARY KEY,
	ten NVARCHAR(250) NOT NULL,
	trang_thai_xoa BIT DEFAULT 0,
	ngay_tao DATE NOT NULL DEFAULT GETDATE(),
	ngay_cap_nhat DATE DEFAULT GETDATE()
)


CREATE TABLE ChatLieu (
	id INT IDENTITY(1, 1) PRIMARY KEY,
	ten NVARCHAR(250) NOT NULL,
	trang_thai_xoa BIT DEFAULT 0,
	ngay_tao DATE NOT NULL DEFAULT GETDATE(),
	ngay_cap_nhat DATE DEFAULT GETDATE()
)

CREATE TABLE XuatXu (
	id INT IDENTITY(1, 1) PRIMARY KEY,
	ten NVARCHAR(250) NOT NULL,
	trang_thai_xoa BIT DEFAULT 0,
	ngay_tao DATE NOT NULL DEFAULT GETDATE(),
	ngay_cap_nhat DATE DEFAULT GETDATE(),
)

CREATE TABLE MauSac (
	id INT IDENTITY(1, 1) PRIMARY KEY,
	ten NVARCHAR(250) NOT NULL,
	trang_thai_xoa BIT DEFAULT 0,
	ngay_tao DATE NOT NULL DEFAULT GETDATE(),
	ngay_cap_nhat DATE DEFAULT GETDATE()
)

CREATE TABLE KichCo (
	id INT IDENTITY(1, 1) PRIMARY KEY,
	ten NVARCHAR(250) NOT NULL,
	trang_thai_xoa BIT DEFAULT 0,
	ngay_tao DATE NOT NULL DEFAULT GETDATE(),
	ngay_cap_nhat DATE DEFAULT GETDATE()
)

CREATE TABLE ThuongHieu (
	id INT IDENTITY(1, 1) PRIMARY KEY,
	ten NVARCHAR(250) NOT NULL,
	trang_thai_xoa BIT DEFAULT 0,
	ngay_tao DATE NOT NULL DEFAULT GETDATE(),
	ngay_cap_nhat DATE DEFAULT GETDATE()
)

CREATE TABLE KieuDang (
	id INT IDENTITY(1, 1) PRIMARY KEY,
	ten NVARCHAR(250) NOT NULL,
	trang_thai_xoa BIT DEFAULT 0,
	ngay_tao DATE NOT NULL DEFAULT GETDATE(),
	ngay_cap_nhat DATE DEFAULT GETDATE()
)

CREATE TABLE DeGiay (
	id INT IDENTITY(1, 1) PRIMARY KEY,
	ten NVARCHAR(250) NOT NULL,
	trang_thai_xoa BIT DEFAULT 0,
	ngay_tao DATE NOT NULL DEFAULT GETDATE(),
	ngay_cap_nhat DATE DEFAULT GETDATE()
);

CREATE TABLE SanPham (
	id INT IDENTITY(1, 1) PRIMARY KEY,
	id_danh_muc INT NOT NULL,
	id_thuong_hieu INT NOT NULL,
	id_xuat_xu INT NOT NULL,
	id_kieu_dang INT NOT NULL,
	id_chat_lieu INT NOT NULL,
	id_de_giay INT NOT NULL,
	ma VARCHAR(50) NOT NULL UNIQUE,
	ten NVARCHAR(250) NOT NULL,
	mo_ta NVARCHAR(2000),
	gia_nhap MONEY NOT NULL,
	gia_ban MONEY NOT NULL,
	hinh_anh VARCHAR(250) NOT NULL,
	trang_thai BIT NOT NULL,
	ngay_tao DATE NOT NULL DEFAULT GETDATE(),
	ngay_cap_nhat DATE DEFAULT GETDATE(),
	trang_thai_xoa BIT DEFAULT 0,
	FOREIGN KEY(id_danh_muc) REFERENCES dbo.DanhMuc(id),
	FOREIGN KEY(id_thuong_hieu) REFERENCES dbo.ThuongHieu(id),
	FOREIGN KEY(id_xuat_xu) REFERENCES dbo.XuatXu(id),
	FOREIGN KEY(id_kieu_dang) REFERENCES dbo.KieuDang(id),
	FOREIGN KEY(id_chat_lieu) REFERENCES dbo.ChatLieu(id),
	FOREIGN KEY(id_de_giay) REFERENCES dbo.DeGiay(id)
)

CREATE TABLE SanPhamChiTiet (
	id INT IDENTITY(1, 1) PRIMARY KEY,
	id_san_pham INT NOT NULL,
	id_kich_co INT NOT NULL,
	id_mau_sac INT NOT NULL,
	ma VARCHAR(50) NOT NULL UNIQUE,
	so_luong INT NOT NULL DEFAULT 0,
	trang_thai BIT NOT NULL DEFAULT 1,
	ngay_tao DATE NOT NULL DEFAULT GETDATE(),
	ngay_cap_nhat DATE DEFAULT GETDATE(),
	trang_thai_xoa BIT DEFAULT 0,
	FOREIGN KEY(id_san_pham) REFERENCES dbo.SanPham(id),
	FOREIGN KEY(id_kich_co) REFERENCES dbo.KichCo(id),
	FOREIGN KEY(id_mau_sac) REFERENCES dbo.MauSac(id)
)

CREATE TABLE PhieuGiamGia (
	id INT IDENTITY(1, 1) PRIMARY KEY,
	ma VARCHAR(50) NOT NULL,
	ten NVARCHAR(250) NOT NULL,
	so_luong INT NOT NULL DEFAULT 0,
	ngay_bat_dau DATE NOT NULL,
	ngay_ket_thuc DATE NOT NULL,
	giam_theo_phan_tram BIT NOT NULL,
	gia_tri_giam FLOAT NOT NULL,
	don_toi_thieu MONEY NOT NULL DEFAULT 0,
	giam_toi_da MONEY NOT NULL DEFAULT 0,
	ngay_tao DATE NOT NULL DEFAULT GETDATE(),
	ngay_cap_nhat DATE DEFAULT GETDATE(),
	trang_thai_xoa BIT DEFAULT 0
)

CREATE TABLE HoaDon (
	id INT IDENTITY(1, 1) PRIMARY KEY,
	id_tai_khoan INT NOT NULL,
	id_khach_hang INT,
	id_phieu_giam_gia INT,
	tien_giam MONEY DEFAULT 0,
	tien_mat MONEY NOT NULL DEFAULT 0,
	tien_chuyen_khoan MONEY NOT NULL DEFAULT 0,
	ma VARCHAR(50) NOT NULL UNIQUE,
	phuong_thuc_thanh_toan TINYINT NOT NULL,
	trang_thai TINYINT NOT NULL,
	ngay_tao DATETIME NOT NULL DEFAULT GETDATE(),
	ngay_cap_nhat DATETIME NOT NULL DEFAULT GETDATE(),
	trang_thai_xoa BIT DEFAULT 0,
	FOREIGN KEY(id_tai_khoan) REFERENCES dbo.TaiKhoan(id),
	FOREIGN KEY(id_khach_hang) REFERENCES dbo.KhachHang(id),
	FOREIGN KEY(id_phieu_giam_gia) REFERENCES dbo.PhieuGiamGia(id)
)

CREATE TABLE HoaDonChiTiet (
	id INT IDENTITY(1, 1) PRIMARY KEY,
	id_san_pham_chi_tiet INT NOT NULL,
	id_hoa_don INT NOT NULL,
	ma VARCHAR(50) NOT NULL UNIQUE,
	gia_nhap MONEY NOT NULL DEFAULT 0,
	gia_ban MONEY NOT NULL DEFAULT 0,
	so_luong INT NOT NULL DEFAULT 0,
	FOREIGN KEY(id_san_pham_chi_tiet) REFERENCES dbo.SanPhamChiTiet(id),
	FOREIGN KEY(id_hoa_don) REFERENCES dbo.HoaDon(id)
)

INSERT INTO dbo.TaiKhoan
(tai_khoan, mat_khau, email, ho_ten, sdt, gioi_tinh, dia_chi, hinh_anh, otp, trang_thai, adm, ngay_tao, trang_thai_xoa)
VALUES
(
	'admin',       -- username - varchar(25)
    N'123',      -- password - nvarchar(50)
    N'bimbimskim@gmail.com',      -- email - nvarchar(250)
    N'nguyễn tùng lâm',      -- ho_ten - nvarchar(250)
    '1111111111',       -- sdt - varchar(13)
    1,     -- gioi_tinh - bit
    N'',      -- dia_chi - nvarchar(250)
    N'',      -- avatar - nvarchar(250)
    '',       -- otp - varchar(6)
    1,     -- trang_thai - bit
    1,     -- is_admin - bit
    GETDATE(), -- ngay_tao - date
	0 -- trang_thai_xoa - bit
),
(
	'user',       -- username - varchar(25)
    N'123',      -- password - nvarchar(50)
    N'email@gmail.com',      -- email - nvarchar(250)
    N'nguyễn tùng lâm',      -- ho_ten - nvarchar(250)
    '2222222222',       -- sdt - varchar(13)
    1,     -- gioi_tinh - bit
    N'',      -- dia_chi - nvarchar(250)
    N'',      -- avatar - nvarchar(250)
    '',       -- otp - varchar(6)
    1,     -- trang_thai - bit
    0,     -- is_admin - bit
    GETDATE(), -- ngay_tao - date
	0 -- trang_thai_xoa - bit
)

INSERT INTO dbo.DanhMuc
(
    ten,
    trang_thai_xoa,
    ngay_tao,
    ngay_cap_nhat
)
VALUES
(   N'Giày thể thao',       -- ten - nvarchar(250)
    0,      -- trang_thai_xoa - bit
    GETDATE(), -- ngay_tao - date
    GETDATE()  -- ngay_cap_nhat - date
),
(   N'Giày thời trang',       -- ten - nvarchar(250)
    0,      -- trang_thai_xoa - bit
    GETDATE(), -- ngay_tao - date
    GETDATE()  -- ngay_cap_nhat - date
)

INSERT INTO dbo.ThuongHieu
(
    ten,
    trang_thai_xoa,
    ngay_tao,
    ngay_cap_nhat
)
VALUES
(   N'Adidas',       -- ten - nvarchar(250)
    0,      -- trang_thai_xoa - bit
    GETDATE(), -- ngay_tao - date
    GETDATE()  -- ngay_cap_nhat - date
    )
,(   N'Puma',       -- ten - nvarchar(250)
    0,      -- trang_thai_xoa - bit
    GETDATE(), -- ngay_tao - date
    GETDATE()  -- ngay_cap_nhat - date
    )

INSERT INTO dbo.XuatXu
(
    ten,
    trang_thai_xoa,
    ngay_tao,
    ngay_cap_nhat
)
VALUES
(   N'Nhật bản',       -- ten - nvarchar(250)
    0,      -- trang_thai_xoa - bit
    GETDATE(), -- ngay_tao - date
    GETDATE()  -- ngay_cap_nhat - date
    ),
	(   N'Việt nam',       -- ten - nvarchar(250)
    0,      -- trang_thai_xoa - bit
    GETDATE(), -- ngay_tao - date
    GETDATE()  -- ngay_cap_nhat - date
    )

INSERT INTO dbo.KieuDang
(
    ten,
    trang_thai_xoa,
    ngay_tao,
    ngay_cap_nhat
)
VALUES
(   N'Nam',       -- ten - nvarchar(250)
    0,      -- trang_thai_xoa - bit
    GETDATE(), -- ngay_tao - date
    GETDATE()  -- ngay_cap_nhat - date
    ),
	(   N'Nữ',       -- ten - nvarchar(250)
    0,      -- trang_thai_xoa - bit
    GETDATE(), -- ngay_tao - date
    GETDATE()  -- ngay_cap_nhat - date
    )

INSERT INTO dbo.ChatLieu
(
    ten,
    trang_thai_xoa,
    ngay_tao,
    ngay_cap_nhat
)
VALUES
(   N'Cao cấp',       -- ten - nvarchar(250)
    0,      -- trang_thai_xoa - bit
    GETDATE(), -- ngay_tao - date
    GETDATE()  -- ngay_cap_nhat - date
    ),
(   N'Real 1:1',       -- ten - nvarchar(250)
    0,      -- trang_thai_xoa - bit
    GETDATE(), -- ngay_tao - date
    GETDATE()  -- ngay_cap_nhat - date
    )

INSERT INTO dbo.DeGiay
(
    ten,
    trang_thai_xoa,
    ngay_tao,
    ngay_cap_nhat
)
VALUES
(   N'Cao',       -- ten - nvarchar(250)
    0,      -- trang_thai_xoa - bit
    GETDATE(), -- ngay_tao - date
    GETDATE()  -- ngay_cap_nhat - date
    ),
	(   N'Thấp',       -- ten - nvarchar(250)
    0,      -- trang_thai_xoa - bit
    GETDATE(), -- ngay_tao - date
    GETDATE()  -- ngay_cap_nhat - date
    )

INSERT INTO dbo.KichCo
(
    ten,
    trang_thai_xoa,
    ngay_tao,
    ngay_cap_nhat
)
VALUES
(   N'L',       -- ten - nvarchar(250)
    0,      -- trang_thai_xoa - bit
    GETDATE(), -- ngay_tao - date
    GETDATE()  -- ngay_cap_nhat - date
    ),
	(   N'XL',       -- ten - nvarchar(250)
    0,      -- trang_thai_xoa - bit
    GETDATE(), -- ngay_tao - date
    GETDATE()  -- ngay_cap_nhat - date
    )

INSERT INTO dbo.MauSac
(
    ten,
    trang_thai_xoa,
    ngay_tao,
    ngay_cap_nhat
)
VALUES
(   N'Đen',       -- ten - nvarchar(250)
    0,      -- trang_thai_xoa - bit
    GETDATE(), -- ngay_tao - date
    GETDATE()  -- ngay_cap_nhat - date
    ),
	(   N'Trắng',       -- ten - nvarchar(250)
    0,      -- trang_thai_xoa - bit
    GETDATE(), -- ngay_tao - date
    GETDATE()  -- ngay_cap_nhat - date
    )