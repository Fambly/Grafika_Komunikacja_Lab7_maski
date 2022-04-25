package company;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import static java.util.Collections.sort;

public class Main {
    public static JFrame frame;
    public static sztaluga sztlg = new sztaluga();

    public static BufferedImage img1;
    public static BufferedImage img_prod;

    public static Color ninja = new Color(238, 238, 238);
    public static final String pi = "[Pion]";
    public static final String pz = "[Poz.]";
    public static String slctd = "";
    public static String[] opcje = {"Wybierz", "Roberts "+pi, "Roberts "+pz, "Prewitt "+pi,
    "Prewitt "+pz, "Sobel "+pi, "Sobel "+pz, "Laplace", "Min.", "Max.", "Medianowy"};
    public static int[][] wzor = new int[3][3];

    public static void main(String[] args) {
        //wczytuję obraz
        try {
            img1 = ImageIO.read(new File("carcrash.jpg"));
        } catch (IOException e) {
            System.out.println("ERROR "+e+", najprawdopodobniej nie wczytano obrazka!");
        }

        //tworzę okno i elementy
        frame = new JFrame("Niezoptymalizowany photoshop BIEDA edyszyn");
        frame.setSize(630, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JLabel label1 = new JLabel();
        label1.setBounds(15,100,250,20);
        frame.add(label1);

        JTextArea TA1 = new JTextArea();
        TA1.setBounds(15, 120, 220, 20);
        TA1.setBackground(ninja);
        frame.add(TA1);

        //zmieniam etykiety oraz pola tekstowe w zależności od wyboru użytkownika
        JComboBox<String> JCB = new JComboBox<>(opcje);
        JCB.setBounds(15, 60, 220, 20);
        JCB.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                slctd = (String) JCB.getSelectedItem();
                if (slctd == null) return;
                switch (slctd) {
                    case "Roberts "+pi -> {
                        wzor = new int[][]{{0, 0, 0},
                                {0, 1, 0},
                                {0, -1, 0}};
                        label1.setText("");
                        TA1.setBackground(ninja);
                        TA1.setText("");
                    }
                    case "Roberts "+pz -> {
                        wzor = new int[][]{{0, 0, 0},
                                {0, 1, -1},
                                {0, 0, 0}};
                        label1.setText("");
                        TA1.setBackground(ninja);
                        TA1.setText("");
                    }
                    case "Prewitt "+pi -> {
                        wzor = new int[][]{{1, 0, -1},
                                {1, 0, -1},
                                {1, 0, -1}};
                        label1.setText("");
                        TA1.setBackground(ninja);
                        TA1.setText("");
                    }
                    case "Prewitt "+pz -> {
                        wzor = new int[][]{{1, 1, 1},
                                {0, 0, 0},
                                {-1, -1, -1}};
                        label1.setText("");
                        TA1.setBackground(ninja);
                        TA1.setText("");
                    }
                    case "Sobel "+pi -> {
                        wzor = new int[][]{{1, 0, -1},
                                {2, 0, -2},
                                {1, 0, -1}};
                        label1.setText("");
                        TA1.setBackground(ninja);
                        TA1.setText("");
                    }
                    case "Sobel "+pz -> {
                        wzor = new int[][]{{1, 2, 1},
                                {0, 0, 0},
                                {-1, -2, -1}};
                        label1.setText("");
                        TA1.setBackground(ninja);
                        TA1.setText("");
                    }
                    case "Laplace" -> {
                        wzor = new int[][]{{0, -1, 0},
                                {-1, 4, -1},
                                {0, -1, 0}};
                        label1.setText("");
                        TA1.setBackground(ninja);
                        TA1.setText("");
                    }
                    case "Wybierz" -> {
                        label1.setText("");
                        TA1.setBackground(ninja);
                        TA1.setText("");
                    }
                    default -> {
                        label1.setText("Promień:");
                        TA1.setBackground(Color.white);
                        TA1.setText("2");
                    }
                }
            }
        });
        frame.add(JCB);

        //Tworzę guzik który po naciśnięciu wykona określoną operację
        JButton btn = new JButton();
        btn.setText("Podgląd");
        btn.setBounds(65, 15, 100, 30);
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switch (slctd) {
                    case "Min." -> {
                        int min_r = 1;
                        try {
                            min_r = Integer.parseInt(TA1.getText());
                            label1.setText("Promień:");
                        } catch (Exception expt) {
                            label1.setText("Promień:     ZŁA WARTOŚĆ!");
                        }
                        minimini(min_r);
                    }
                    case "Max." -> {
                        int max_r = 1;
                        try {
                            max_r = Integer.parseInt(TA1.getText());
                            label1.setText("Promień:");
                        } catch (Exception expt) {
                            label1.setText("Promień:     ZŁA WARTOŚĆ!");
                        }
                        maxf(max_r);
                    }
                    case "Medianowy" -> {
                        int med_r = 1;
                        try {
                            med_r = Integer.parseInt(TA1.getText());
                            label1.setText("Promień:");
                        } catch (Exception expt) {
                            label1.setText("Promień:     ZŁA WARTOŚĆ!");
                        }
                        medik(med_r);
                    }
                    case "Wybierz" -> img_prod = img1;
                    default -> nazwiska(wzor);
                }
            }
        });
        frame.add(btn);

        frame.add(sztlg);
        frame.setVisible(true);
    }

    //metoda służąca do kopiowania obrazu i modyfikacji go bez naruszania wartości orginału
    public static BufferedImage cache_img(BufferedImage buff_img) {
        ColorModel cm = buff_img.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = buff_img.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    //Kolejno stosowanie wzorów z lab7.pdf
    public static void minimini(int r){
        img_prod = cache_img(img1);

        for (int i = r; i < img_prod.getWidth()-r; i++) {
            for (int j = r; j < img_prod.getHeight()-r; j++) {
                int red = 255;
                int green = 255;
                int blue = 255;

                for (int k = -r; k <= r; k++) {
                    for (int l = -r; l <= r; l++) {
                        Color prim = new Color(img1.getRGB(i+k, j+l));

                        if (red > prim.getRed())
                            red = prim.getRed();
                        if (green > prim.getGreen())
                            green = prim.getGreen();
                        if (blue > prim.getBlue())
                            blue = prim.getBlue();
                    }
                }
                if (red > 255) red = 255;
                else if (red < 0) red = 0;
                if (green > 255) green = 255;
                else if (green < 0) green = 0;
                if (blue > 255) blue = 255;
                else if (blue < 0) blue = 0;

                Color fin = new Color(red, green, blue);
                img_prod.setRGB(i, j, fin.getRGB());
            }
        }
        sztlg.repaint();
    }

    public static void maxf(int r){
        img_prod = cache_img(img1);

        for (int i = r; i < img_prod.getWidth()-r; i++) {
            for (int j = r; j < img_prod.getHeight()-r; j++) {
                int red = 0;
                int green = 0;
                int blue = 0;

                for (int k = -r; k <= r; k++) {
                    for (int l = -r; l <= r; l++) {
                        Color prim = new Color(img1.getRGB(i+k, j+l));

                        if (red < prim.getRed())
                            red = prim.getRed();
                        if (green < prim.getGreen())
                            green = prim.getGreen();
                        if (blue < prim.getBlue())
                            blue = prim.getBlue();
                    }
                }
                if (red > 255) red = 255;
                else if (red < 0) red = 0;
                if (green > 255) green = 255;
                else if (green < 0) green = 0;
                if (blue > 255) blue = 255;
                else if (blue < 0) blue = 0;

                Color fin = new Color(red, green, blue);
                img_prod.setRGB(i, j, fin.getRGB());
            }
        }
        sztlg.repaint();
    }
    public static void medik(int r){
        img_prod = cache_img(img1);

        for (int i = r; i < img_prod.getWidth()-r; i++) {
            for (int j = r; j < img_prod.getHeight()-r; j++) {
                int red = 0;
                int green = 0;
                int blue = 0;
                ArrayList<Integer> R = new ArrayList<>();
                ArrayList<Integer> G = new ArrayList<>();
                ArrayList<Integer> B = new ArrayList<>();

                for (int k = -r; k <= r; k++) {
                    for (int l = -r; l <= r; l++) {
                        Color prim = new Color(img1.getRGB(i+k, j+l));
                        R.add(prim.getRed());
                        G.add(prim.getGreen());
                        B.add(prim.getBlue());
                    }
                }
                sort(R);
                if (R.size() % 2 == 0)
                    red = R.get(R.size() / 2) + R.get(R.size() / 2 - 1) / 2;
                else red = R.get(R.size() / 2);

                sort(G);
                if (G.size() % 2 == 0)
                    green = G.get(G.size() / 2) + G.get(G.size() / 2 - 1) / 2;
                else green = G.get(G.size() / 2);

                sort(B);
                if (B.size() % 2 == 0)
                    blue = B.get(B.size() / 2) + B.get(B.size() / 2 - 1) / 2;
                else blue = B.get(B.size() / 2);

                if (red > 255) red = 255;
                else if (red < 0) red = 0;
                if (green > 255) green = 255;
                else if (green < 0) green = 0;
                if (blue > 255) blue = 255;
                else if (blue < 0) blue = 0;

                Color fin = new Color(red, green, blue);
                img_prod.setRGB(i, j, fin.getRGB());
            }
        }
        sztlg.repaint();
    }

    public static void nazwiska(int[][] mtrx){
        img_prod = cache_img(img1);

        for (int i = 1; i < img_prod.getWidth()-1; i++) {
            for (int j = 1; j < img_prod.getHeight()-1; j++) {
                int red = 0;
                int green = 0;
                int blue = 0;

                for (int k = -1; k <= 1 ; k++) {
                    for (int l = -1; l <= 1; l++) {
                        Color prim = new Color(img1.getRGB(i+k,j+l));

                        red += prim.getRed() * mtrx[k+1][l+1];
                        green += prim.getGreen() * mtrx[k+1][l+1];
                        blue += prim.getBlue() * mtrx[k+1][l+1];
                    }
                }
                if (red > 255) red = 255;
                else if (red < 0) red = 0;
                if (green > 255) green = 255;
                else if (green < 0) green = 0;
                if (blue > 255) blue = 255;
                else if (blue < 0) blue = 0;

                Color fin = new Color(red, green, blue);
                img_prod.setRGB(i, j, fin.getRGB());
            }
        }
        sztlg.repaint();
    }
}

class sztaluga extends Canvas {
    @Override
    public void paint(Graphics g) {
        Graphics2D G2D = (Graphics2D) g;
        G2D.drawImage(Main.img1, 300, 15, null);
        G2D.drawImage(Main.img_prod, 300, 330, null);
    }
}