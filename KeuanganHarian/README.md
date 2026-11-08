# Keuangan Harian — Android Studio

Aplikasi pencatatan pemasukan dan pengeluaran harian dengan tampilan putih-biru.

## Fitur
- Ringkasan total saldo, pemasukan, dan pengeluaran.
- Tambah transaksi pemasukan atau pengeluaran beserta nominal dan keterangan.
- Riwayat transaksi terbaru.
- Pengaturan nama panggilan dan pilihan tampil/sembunyikan saldo.
- Pengaturan tetap tersimpan setelah aplikasi ditutup paksa dan dibuka kembali.

## Teknologi dan penyimpanan
- Bahasa: Java.
- Database lokal: SQLite (`TransactionDbHelper`) untuk transaksi, nominal, keterangan, dan waktu.
- SharedPreferences (`PrefsManager`) untuk nama panggilan dan pilihan tampil/sembunyikan saldo.
- `apply()` digunakan saat menyimpan preferensi.
- Preferensi dibaca kembali di `onResume()` agar pengaturan diterapkan saat layar aktif kembali.

## Cara membuka
1. Ekstrak ZIP.
2. Buka Android Studio.
3. Pilih **Open**, lalu pilih folder `KeuanganHarian`.
4. Tunggu proses Gradle Sync selesai.
5. Jalankan aplikasi pada emulator atau HP Android.

Jika Android Studio meminta SDK, pasang Android SDK Platform 35 (atau sesuaikan `compileSdk` dengan SDK yang tersedia).

## Studi kasus tugas P10
**Judul:** SharedPreferences pada Aplikasi Mobile – Keuangan Harian: Pencatatan Pemasukan dan Pengeluaran.

**Masalah:** Pengguna membutuhkan aplikasi sederhana untuk mencatat uang masuk dan keluar, mengetahui saldo, serta mempertahankan preferensi tampilan ketika aplikasi dibuka kembali.

**SharedPreferences:** Menyimpan nama panggilan dan pengaturan tampil/sembunyikan saldo. Data ini bersifat preferensi pengguna, berukuran kecil, dan perlu tetap tersedia setelah aplikasi ditutup.

**Database:** Menyimpan transaksi (jenis, nominal, keterangan, waktu). Riwayat transaksi merupakan data utama yang jumlahnya dapat bertambah dan perlu dikelola sebagai kumpulan data, sehingga disimpan di SQLite, bukan SharedPreferences.

**Alur:** `PrefsManager` membaca/menulis SharedPreferences. Perubahan disimpan dengan `apply()`. `MainActivity.onResume()` membaca ulang preferensi dan memperbarui tampilan.

**Bukti force stop:** Atur nama panggilan dan matikan pilihan tampilkan saldo, simpan. Force stop aplikasi melalui App Info, lalu buka lagi. Nama panggilan tetap tampil dan saldo tetap tersembunyi. Ini membuktikan preferensi tersimpan secara persisten.

## Catatan
Proyek ini adalah source project Android Studio, bukan APK siap instal. Untuk menghasilkan APK: Android Studio → Build → Build Bundle(s) / APK(s) → Build APK(s).
