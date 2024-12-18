package com.spotify;
import java.math.BigDecimal;
import java.sql.Time;
import java.time.LocalTime;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        DbFunctions db = new DbFunctions();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            try {
                System.out.println("\n--- İşlem Seçin ---");
                System.out.println("1. İçerik Ekle");
                System.out.println("2. İçerik Ara");
                System.out.println("3. İçerik Sil");
                System.out.println("4. İçerik Türüne Göre Sayı");
                System.out.println("5. Sanatçıya Göre İçerik Listele");
                System.out.println("6. İçerik Güncelle");
                System.out.println("7. Şikayet Ekle");
                System.out.println("8. Ödeme Ekle");
                System.out.println("9. Dinleme Ekle");
                System.out.println("10. Değerlendirme Ekle");
                System.out.println("11. Çıkış");

                System.out.print("Seçiminizi yapın (1/2/3/4/5/6/7/8/9/10/11): ");
                int secim = scanner.nextInt();
                scanner.nextLine(); // buffer'ı temizle

                switch (secim) {
                    case 1:
                        try {
                            System.out.print("Kullanıcı ID'nizi girin: ");
                            int kullaniciId;
                            try {
                                kullaniciId = scanner.nextInt();
                                scanner.nextLine(); // buffer'ı temizle
                            } catch (InputMismatchException e) {
                                System.out.println("Geçersiz Kullanıcı ID'si! Sayı giriniz.");
                                scanner.nextLine(); // buffer'ı temizle
                                break;
                            }

                            System.out.println("Eklemek İstediğiniz İçeriğin Adı:");
                            String adi = scanner.nextLine();

                            System.out.print("İçeriğin türünü girin (s: Şarkı, a: Albüm, p: Podcast): ");
                            char turu = scanner.nextLine().toLowerCase().charAt(0);

                            if (turu != 's' && turu != 'a' && turu != 'p') {
                                System.out.println("Geçersiz tür! Lütfen s, a veya p giriniz.");
                                break;
                            }

                            System.out.print("İçeriğin süresini girin (saniye cinsinden): ");
                            int sure;
                            try {
                                sure = scanner.nextInt();
                                scanner.nextLine(); // buffer'ı temizle
                            } catch (InputMismatchException e) {
                                System.out.println("Geçersiz süre! Sayı giriniz.");
                                scanner.nextLine(); // buffer'ı temizle
                                break;
                            }

                            int albumId = 0;
                            if (turu == 's') {
                                System.out.print("Albüm ID'sini girin: ");
                                try {
                                    albumId = scanner.nextInt();
                                    scanner.nextLine(); // buffer'ı temizle
                                } catch (InputMismatchException e) {
                                    System.out.println("Geçersiz Albüm ID'si! Sayı giriniz.");
                                    scanner.nextLine(); // buffer'ı temizle
                                    break;
                                }
                            }

                            System.out.print("Sanatçı ID'sini girin: ");
                            int sanatciId;
                            try {
                                sanatciId = scanner.nextInt();
                                scanner.nextLine(); // buffer'ı temizle
                            } catch (InputMismatchException e) {
                                System.out.println("Geçersiz Sanatçı ID'si! Sayı giriniz.");
                                scanner.nextLine(); // buffer'ı temizle
                                break;
                            }

                            // Kullanıcı ID'si ile birlikte içerik ekleme
                            db.insertIcerik(adi, sure, albumId, sanatciId, turu, kullaniciId);
                            break;
                        } catch (Exception e) {
                            System.out.println("İşlem sırasında bir hata oluştu: " + e.getMessage());
                            break;
                        }
                    case 2:
                        System.out.print("Aramak istediğiniz içeriğin başlığını girin: ");
                        String aramaTerimi = scanner.nextLine();
                        db.searchIcerik(aramaTerimi);  // İçerikleri ara
                        break;

                    case 3:
                        System.out.print("Silmek istediğiniz içeriğin ID'sini girin: ");
                        try {
                            int icerikId = scanner.nextInt();
                            db.deleteIcerik(icerikId);  // İçeriği sil
                        } catch (InputMismatchException e) {
                            System.out.println("Geçersiz ID! Sayı giriniz.");
                            scanner.nextLine(); // buffer'ı temizle
                        }
                        break;

                    case 4:
                        System.out.print("İçerik türünü girin (s: Şarkı, a: Albüm, p: Podcast): ");
                        char sayimTuru = scanner.nextLine().toLowerCase().charAt(0);
                        if (sayimTuru != 's' && sayimTuru != 'a' && sayimTuru != 'p') {
                            System.out.println("Geçersiz tür! Lütfen s, a veya p giriniz.");
                        } else {
                            db.icerikTuruGoreSay(sayimTuru);  // İçerik türüne göre içerik sayısını listele
                        }
                        break;

                    case 5:
                        System.out.print("Sanatçı ID'sini girin: ");
                        try {
                            int sanatciIdList = scanner.nextInt();
                            db.sanatciyaGoreIcerikListele(sanatciIdList);  // Sanatçıya göre içerik listele
                        } catch (InputMismatchException e) {
                            System.out.println("Geçersiz Sanatçı ID'si! Sayı giriniz.");
                            scanner.nextLine(); // buffer'ı temizle
                        }
                        break;

                    case 6:
                        System.out.print("Güncellemek istediğiniz içerik ID'sini girin: ");
                        int guncelleIcerikId;
                        try {
                            guncelleIcerikId = scanner.nextInt();
                        } catch (InputMismatchException e) {
                            System.out.println("Geçersiz ID! Sayı giriniz.");
                            scanner.nextLine(); // buffer'ı temizle
                            break;
                        }
                        scanner.nextLine(); // buffer'ı temizle

                        System.out.print("Yeni başlık: ");
                        String yeniBaslik = scanner.nextLine();

                        System.out.print("Yeni süre: ");
                        int yeniSure;
                        try {
                            yeniSure = scanner.nextInt();
                        } catch (InputMismatchException e) {
                            System.out.println("Geçersiz süre! Sayı giriniz.");
                            scanner.nextLine(); // buffer'ı temizle
                            break;
                        }

                        System.out.print("Yeni tür (s: Şarkı, a: Albüm, p: Podcast): ");
                        char yeniTuru = scanner.next().toLowerCase().charAt(0);
                        if (yeniTuru != 's' && yeniTuru != 'a' && yeniTuru != 'p') {
                            System.out.println("Geçersiz tür! Lütfen s, a veya p giriniz.");
                            break;
                        }

                        db.updateIcerik(guncelleIcerikId, yeniBaslik, yeniSure, yeniTuru);  // İçeriği güncelle
                        break;

                    case 7:
                        System.out.print("Kullanıcı ID'sini girin: ");
                        int kullaniciIdSikayet = scanner.nextInt();
                        scanner.nextLine(); // buffer'ı temizle
                        System.out.print("Şikayet içeriğini girin: ");
                        String sikayetIcerik = scanner.nextLine();
                        db.sikayetEkle(kullaniciIdSikayet, sikayetIcerik);
                        break;
                    case 8:
                        System.out.print("Kullanıcı ID'sini girin: ");
                        int kullaniciIdOdeme = scanner.nextInt();
                        System.out.print("Ödeme miktarını girin: ");
                        BigDecimal odemeMiktar = scanner.nextBigDecimal();
                        db.odemeEkle(kullaniciIdOdeme, odemeMiktar);
                        break;
                    case 9:
                        System.out.print("Kullanıcı ID'sini girin: ");
                        int kullaniciIdDinleme = scanner.nextInt();
                        System.out.print("İçerik ID'sini girin: ");
                        int icerikIdDinleme = scanner.nextInt();
                        System.out.print("Dinleme süresini girin (saniye cinsinden): ");
                        int sureDinleme = scanner.nextInt();

                        // Saniye değeri 60'dan büyükse dakika ve saniye olarak bölün
                        int dakika = sureDinleme / 60;
                        int saniye = sureDinleme % 60;

                        // Dinleme süresini dakika ve saniye cinsinden LocalTime olarak oluştur
                        db.dinlemeEkle(kullaniciIdDinleme, icerikIdDinleme, Time.valueOf(LocalTime.of(0, dakika, saniye)));
                        break;
                    case 10:
                        System.out.print("Kullanıcı ID'sini girin: ");
                        int kullaniciIdDegerlendirme = scanner.nextInt();
                        System.out.print("İçerik ID'sini girin: ");
                        int icerikIdDegerlendirme = scanner.nextInt();
                        System.out.print("Puanı girin (1-5): ");
                        int puan = scanner.nextInt();
                        scanner.nextLine(); // buffer'ı temizle
                        System.out.print("Yorumunuzu girin: ");
                        String yorum = scanner.nextLine();
                        db.degerlendirmeEkle(kullaniciIdDegerlendirme, icerikIdDegerlendirme, puan, yorum);
                        break;
                    case 11:
                        System.out.println("Çıkılıyor...");
                        scanner.close();
                        return;
                    default:
                        System.out.println("Geçersiz seçim, tekrar deneyin.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Geçersiz giriş! Lütfen doğru formatta bir değer giriniz.");
                scanner.nextLine(); // buffer'ı temizle
            }
        }
    }
}
