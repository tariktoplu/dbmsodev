package com.spotify;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;

public class DbFunctions {
    public Connection connect_to_db(){
        Connection conn = null;
        try{
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/" + "Spotify", "postgres", "6748");
            if (conn != null) {
                System.out.println("Connected to database");
            } else {
                System.out.println("Failed to connect to database");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return conn;
    }
    public void insertIcerik(String adi, int sure, int album_id, int sanatci_id, char turu, int kullanici_id) {
        Statement statement = null;
        Connection conn = this.connect_to_db();

        try {
            // Kullanıcının rolünü kontrol et
            String queryUserRole = "SELECT rol FROM kullanicilar WHERE kullaniciid = " + kullanici_id + ";";

            statement = conn.createStatement();
            ResultSet rsUserRole = statement.executeQuery(queryUserRole);

            String userRole = "";
            if (rsUserRole.next()) {
                userRole = rsUserRole.getString("rol");
            } else {
                System.out.println("Hata: Kullanıcı bulunamadı.");
                return;
            }

            // Eğer kullanıcı admin değilse, işlemi durdur
            if (!userRole.equalsIgnoreCase("admin")) {
                System.out.println("Hata: Bu işlemi yalnızca admin kullanıcılar yapabilir.");
                return;
            }

            // Admin ise, içerik ekleme işlemine devam et
            String queryIcerikler = "INSERT INTO icerikler (baslik, sure, turu, kullaniciid) VALUES ('" + adi + "', " + sure + ", '" + turu + "', " + kullanici_id + ") RETURNING icerikid;";
            ResultSet rs = statement.executeQuery(queryIcerikler);

            int icerikId = -1;
            if (rs.next()) {
                icerikId = rs.getInt("icerikid");
            }

            // Kontrol: İçerik eklenemediyse işlemi durdur
            if (icerikId == -1) {
                throw new Exception("İçerik eklenemedi.");
            }

            // Tür 's' (şarkı) ise, şarkılar tablosuna veri ekleme
            if (turu == 's') {
                String querySarkilar = "INSERT INTO sarkilar (icerikid, albumid) VALUES (" + icerikId + ", " + album_id + ");";
                statement.executeUpdate(querySarkilar);

                // Albümdeki şarkı sayısını 1 arttır (albumler tablosundaki icerikid'yi kullanarak)
                String queryUpdateAlbum = "UPDATE albumler SET sarkisayisi = sarkisayisi + 1 WHERE icerikid = " + album_id + ";";
                statement.executeUpdate(queryUpdateAlbum);
            }
            // Tür 'a' (albüm) ise, albümler tablosuna veri ekleme
            else if (turu == 'a') {
                String queryAlbumler = "INSERT INTO albumler (icerikid) VALUES (" + icerikId + ");";
                statement.executeUpdate(queryAlbumler);
            }
            // Tür 'p' (podcast) ise, podcastler tablosuna veri ekleme
            else if (turu == 'p') {
                String queryPodcastler = "INSERT INTO podcastler (icerikid) VALUES (" + icerikId + ");";
                statement.executeUpdate(queryPodcastler);
            }

            // 3. Ara tabloya sanatçı ile ilişkilendirme (iceriksanatci tablosu)
            String queryAraTablo = "INSERT INTO iceriksanatci (sanatciid, icerikid) VALUES (" + sanatci_id + ", " + icerikId + ");";
            statement.executeUpdate(queryAraTablo);

            System.out.println("Yeni içerik eklendi: " + adi);

        } catch (Exception e) {
            System.out.println("Hata: " + e.getMessage());
        } finally {
            try {
                if (statement != null) statement.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                System.out.println("Bağlantı kapatma hatası: " + e.getMessage());
            }
        }
    }


    public void searchIcerik(String aramaTerimi) {
        Statement statement = null;
        Connection conn = this.connect_to_db();

        try {
            String querySearch = "SELECT * FROM icerikler WHERE baslik LIKE '%" + aramaTerimi + "%';";
            statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(querySearch);

            boolean found = false;
            while (rs.next()) {
                found = true;
                int icerikId = rs.getInt("icerikid");
                String baslik = rs.getString("baslik");
                int sure = rs.getInt("sure");
                char turu = rs.getString("turu").charAt(0);
                System.out.println("İçerik ID: " + icerikId + ", Başlık: " + baslik + ", Süre: " + sure + " saniye, Türü: " + turu);
            }

            if (!found) {
                System.out.println("Aradığınız kriterlere uygun içerik bulunamadı.");
            }

        } catch (Exception e) {
            System.out.println("Hata: " + e.getMessage());
        } finally {
            try {
                if (statement != null) statement.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                System.out.println("Bağlantı kapatma hatası: " + e.getMessage());
            }
        }
    }
    public void deleteIcerik(int icerikId) {
        Statement statement = null;
        Connection conn = this.connect_to_db();

        try {
            // 1. İçerik ID'sinin var olup olmadığını kontrol et
            String queryCheckIcerikExists = "SELECT * FROM icerikler WHERE icerikid = " + icerikId + ";";
            statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(queryCheckIcerikExists);

            // Eğer içerik mevcut değilse, işlem yapılmaz
            if (!rs.next()) {
                System.out.println("Hata: Böyle bir içerik bulunamadı.");
                return;  // İşlemi sonlandır
            }

            // 2. İçerik mevcutsa, sırasıyla silme işlemi başlatılacak
            // İlk olarak, şarkılar tablosundan ilgili içerik ID'sini sil
            String queryCheckIfSongInAlbum = "SELECT albumid FROM sarkilar WHERE icerikid = " + icerikId + ";";
            ResultSet rsSongInAlbum = statement.executeQuery(queryCheckIfSongInAlbum);

            // Eğer şarkı bir albüme aitse, albümdeki şarkı sayısını azalt
            if (rsSongInAlbum.next()) {
                int albumId = rsSongInAlbum.getInt("albumid");
                String queryUpdateAlbum = "UPDATE albumler SET sarkisayisi = sarkisayisi - 1 WHERE icerikid = " + albumId + ";";
                statement.executeUpdate(queryUpdateAlbum);
                System.out.println("Albümdeki şarkı sayısı bir azaltıldı.");
            }

            // Şarkıyı sarkilar tablosundan sil
            String queryDeleteSarkilar = "DELETE FROM sarkilar WHERE icerikid = " + icerikId + ";";
            statement.executeUpdate(queryDeleteSarkilar);
            System.out.println("Şarkılar tablosundan içerik silindi.");

            // 3. iceriksanatci tablosundan ilgili içerik ID'sini sil
            String queryDeleteIcerikSanatci = "DELETE FROM iceriksanatci WHERE icerikid = " + icerikId + ";";
            statement.executeUpdate(queryDeleteIcerikSanatci);
            System.out.println("Sanatçı-İçerik ilişkisi tablosundan içerik silindi.");

            // 4. Son olarak, içerikler tablosundan ilgili içerik ID'sini sil
            String queryDeleteIcerikler = "DELETE FROM icerikler WHERE icerikid = " + icerikId + ";";
            statement.executeUpdate(queryDeleteIcerikler);
            System.out.println("İçerikler tablosundan içerik silindi.");

        } catch (Exception e) {
            System.out.println("Hata: " + e.getMessage());
        } finally {
            try {
                if (statement != null) statement.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                System.out.println("Bağlantı kapatma hatası: " + e.getMessage());
            }
        }
    }

    public void icerikTuruGoreSay(char turu) {
        Statement statement = null;
        Connection conn = this.connect_to_db();

        try {
            String query = "SELECT icerik_turu_gore_say('" + turu + "');";
            statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(query);

            if (rs.next()) {
                System.out.println("İçerik türü " + turu + " olan içerik sayısı: " + rs.getInt(1));
            } else {
                System.out.println("Veri bulunamadı.");
            }
        } catch (Exception e) {
            System.out.println("Hata: " + e.getMessage());
        } finally {
            try {
                if (statement != null) statement.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                System.out.println("Bağlantı kapatma hatası: " + e.getMessage());
            }
        }
    }public void sanatciyaGoreIcerikListele(int sanatciId) {
        Statement statement = null;
        Connection conn = this.connect_to_db();

        try {
            String query = "SELECT * FROM sanatciya_gore_icerik_listele(" + sanatciId + ");";
            statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(query);

            boolean found = false;
            while (rs.next()) {
                found = true;
                int icerikId = rs.getInt("icerikid");
                String baslik = rs.getString("baslik");
                int sure = rs.getInt("sure");
                char turu = rs.getString("turu").charAt(0);
                System.out.println("İçerik ID: " + icerikId + ", Başlık: " + baslik + ", Süre: " + sure + " saniye, Türü: " + turu);
            }

            if (!found) {
                System.out.println("Sanatçıya ait içerik bulunamadı.");
            }

        } catch (Exception e) {
            System.out.println("Hata: " + e.getMessage());
        } finally {
            try {
                if (statement != null) statement.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                System.out.println("Bağlantı kapatma hatası: " + e.getMessage());
            }
        }
    }
    public void updateIcerik(int icerikId, String yeniBaslik, int yeniSure, char yeniTuru) {
        Statement statement = null;
        Connection conn = this.connect_to_db();

        try {
            String query = "UPDATE icerikler SET baslik = '" + yeniBaslik + "', sure = " + yeniSure + ", turu = '" + yeniTuru + "' WHERE icerikid = " + icerikId + ";";
            statement = conn.createStatement();
            int rowsUpdated = statement.executeUpdate(query);

            if (rowsUpdated > 0) {
                System.out.println("İçerik güncellendi.");
            } else {
                System.out.println("İçerik bulunamadı.");
            }
        } catch (Exception e) {
            System.out.println("Hata: " + e.getMessage());
        } finally {
            try {
                if (statement != null) statement.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                System.out.println("Bağlantı kapatma hatası: " + e.getMessage());
            }
        }
    }

}
