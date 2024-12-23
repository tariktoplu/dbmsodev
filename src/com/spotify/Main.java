package com.spotify;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

public class Main {
    private static final DbFunctions db = new DbFunctions();

    public static void main(String[] args) {
        // Ana pencere oluşturma
        JFrame frame = new JFrame("Spotify Yönetim Sistemi");
        frame.setSize(600, 500); // Boyut arttırıldı
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Panel ve düzen
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 1));
        frame.add(panel);

        // Butonlar ve eylemler
        JButton btnIcerikEkle = new JButton("İçerik Ekle");
        btnIcerikEkle.addActionListener(e -> icerikEkleDialog());
        panel.add(btnIcerikEkle);

        JButton btnIcerikAra = new JButton("İçerik Ara");
        btnIcerikAra.addActionListener(e -> icerikAraDialog());
        panel.add(btnIcerikAra);

        JButton btnIcerikSil = new JButton("İçerik Sil");
        btnIcerikSil.addActionListener(e -> icerikSilDialog());
        panel.add(btnIcerikSil);

        JButton btnIcerikGuncelle = new JButton("İçerik Güncelle");
        btnIcerikGuncelle.addActionListener(e -> icerikGuncelleDialog());
        panel.add(btnIcerikGuncelle);

        JButton btnSikayetEkle = new JButton("Şikayet Ekle");
        btnSikayetEkle.addActionListener(e -> sikayetEkleDialog());
        panel.add(btnSikayetEkle);

        JButton btnDegerlendirmeEkle = new JButton("Değerlendirme Ekle");
        btnDegerlendirmeEkle.addActionListener(e -> degerlendirmeEkleDialog());
        panel.add(btnDegerlendirmeEkle);

        JButton btnSanatciyaGoreListele = new JButton("Sanatçıya Göre Listele");
        btnSanatciyaGoreListele.addActionListener(e -> sanatciyaGoreListeleDialog());
        panel.add(btnSanatciyaGoreListele);

        JButton btnOdemeEkle = new JButton("Ödeme Ekle");
        btnOdemeEkle.addActionListener(e -> odemeEkleDialog());
        panel.add(btnOdemeEkle);

        JButton btnCikis = new JButton("Çıkış");
        btnCikis.addActionListener(e -> System.exit(0));
        panel.add(btnCikis);

        // Pencereyi görünür yap
        frame.setVisible(true);
    }

    private static void icerikEkleDialog() {
        JTextField kullaniciIdField = new JTextField();
        JTextField adiField = new JTextField();
        JTextField turuField = new JTextField();
        JTextField sureField = new JTextField();
        JTextField sanatciIdField = new JTextField();
        JTextField albumIdField = new JTextField();

        Object[] inputs = {
                "Kullanıcı ID:", kullaniciIdField,
                "Adı:", adiField,
                "Tür (s, a, p):", turuField,
                "Süre (saniye):", sureField,
                "Sanatçı ID:", sanatciIdField,
                "Albüm ID (Opsiyonel):", albumIdField
        };

        int result = JOptionPane.showConfirmDialog(null, inputs, "İçerik Ekle", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                int kullaniciId = Integer.parseInt(kullaniciIdField.getText());
                String adi = adiField.getText();
                char turu = turuField.getText().toLowerCase().charAt(0);
                int sure = Integer.parseInt(sureField.getText());
                int sanatciId = Integer.parseInt(sanatciIdField.getText());
                Integer albumId = albumIdField.getText().isEmpty() ? null : Integer.parseInt(albumIdField.getText());

                db.insertIcerik(adi, sure, albumId, sanatciId, turu, kullaniciId);
                JOptionPane.showMessageDialog(null, "İçerik başarıyla eklendi!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Hata: " + ex.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static void icerikAraDialog() {
        String baslik = JOptionPane.showInputDialog(null, "Aramak istediğiniz içeriğin başlığı:", "İçerik Ara", JOptionPane.QUESTION_MESSAGE);
        if (baslik != null && !baslik.isEmpty()) {
            db.searchIcerik(baslik);
            JOptionPane.showMessageDialog(null, "Arama tamamlandı! Sonuçlar console'a yazdırıldı.");
        }
    }

    private static void icerikSilDialog() {
        String idStr = JOptionPane.showInputDialog(null, "Silmek istediğiniz içeriğin ID'sini girin:", "İçerik Sil", JOptionPane.QUESTION_MESSAGE);
        if (idStr != null && !idStr.isEmpty()) {
            try {
                int id = Integer.parseInt(idStr);
                db.deleteIcerik(id);
                JOptionPane.showMessageDialog(null, "İçerik başarıyla silindi!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Hata: " + ex.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static void icerikGuncelleDialog() {
        JTextField idField = new JTextField();
        JTextField yeniAdiField = new JTextField();
        JTextField yeniSureField = new JTextField();
        JTextField yeniTuruField = new JTextField();

        Object[] inputs = {
                "İçerik ID:", idField,
                "Yeni Adı:", yeniAdiField,
                "Yeni Süre (saniye):", yeniSureField,
                "Yeni Tür (s, a, p):", yeniTuruField
        };

        int result = JOptionPane.showConfirmDialog(null, inputs, "İçerik Güncelle", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                int id = Integer.parseInt(idField.getText());
                String yeniAdi = yeniAdiField.getText();
                int yeniSure = Integer.parseInt(yeniSureField.getText());
                char yeniTuru = yeniTuruField.getText().toLowerCase().charAt(0);
                db.updateIcerik(id, yeniAdi, yeniSure, yeniTuru);
                JOptionPane.showMessageDialog(null, "İçerik başarıyla güncellendi!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Hata: " + ex.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static void sikayetEkleDialog() {
        JTextField kullaniciIdField = new JTextField();
        JTextField sikayetField = new JTextField();

        Object[] inputs = {
                "Kullanıcı ID:", kullaniciIdField,
                "Şikayet:", sikayetField
        };

        int result = JOptionPane.showConfirmDialog(null, inputs, "Şikayet Ekle", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                int kullaniciId = Integer.parseInt(kullaniciIdField.getText());
                String sikayet = sikayetField.getText();
                db.sikayetEkle(kullaniciId, sikayet);
                JOptionPane.showMessageDialog(null, "Şikayet başarıyla eklendi!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Hata: " + ex.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static void degerlendirmeEkleDialog() {
        JTextField icerikIdField = new JTextField();
        JTextField kullaniciIdField = new JTextField();
        JTextField degerlendirmeField = new JTextField();
        JTextField yorumField = new JTextField();

        Object[] inputs = {
                "İçerik ID:", icerikIdField,
                "Kullanıcı ID:", kullaniciIdField,
                "Değerlendirme (1-5):", degerlendirmeField,
                "Yorum (Opsiyonel):", yorumField
        };

        int result = JOptionPane.showConfirmDialog(null, inputs, "Değerlendirme Ekle", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                int icerikId = Integer.parseInt(icerikIdField.getText());
                int kullaniciId = Integer.parseInt(kullaniciIdField.getText());
                int degerlendirme = Integer.parseInt(degerlendirmeField.getText());
                String yorum = yorumField.getText();
                db.degerlendirmeEkle(icerikId, kullaniciId, degerlendirme, yorum);
                JOptionPane.showMessageDialog(null, "Değerlendirme başarıyla eklendi!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Hata: " + ex.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static void sanatciyaGoreListeleDialog() {
        String sanatciIdStr = JOptionPane.showInputDialog(null, "Sanatçı ID'sini girin:", "Sanatçıya Göre Listele", JOptionPane.QUESTION_MESSAGE);
        if (sanatciIdStr != null && !sanatciIdStr.isEmpty()) {
            try {
                int sanatciId = Integer.parseInt(sanatciIdStr); // Burada sanatçı ID'si bir int'e dönüştürülür
                db.sanatciyaGoreIcerikListele(sanatciId);  // Veritabanındaki içerikleri listele
                JOptionPane.showMessageDialog(null, "Listeleme tamamlandı! Sonuçlar console'a yazdırıldı.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Hata: " + ex.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    private static void odemeEkleDialog() {
        JTextField kullaniciIdField = new JTextField();
        JTextField miktarField = new JTextField();

        Object[] inputs = {
                "Kullanıcı ID:", kullaniciIdField,
                "Ödeme Miktarı:", miktarField
        };

        int result = JOptionPane.showConfirmDialog(null, inputs, "Ödeme Ekle", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                int kullaniciId = Integer.parseInt(kullaniciIdField.getText());
                BigDecimal miktar = new BigDecimal(miktarField.getText());
                db.odemeEkle(kullaniciId, miktar);
                JOptionPane.showMessageDialog(null, "Ödeme başarıyla eklendi!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Hata: " + ex.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
