//20360859033
//Samet Özkan
package fasta;

import java.awt.Color;
import java.awt.Component;
import java.util.Scanner;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
    
public class Fasta extends JFrame{
    
    String sorgu; //Sorgu sekansı.
    String hedef; //Hedef sekans.
    String[] sutunAdlari; //Tablonun sütunlarının adlarının dizisi.
    Object[][] matris; //Dimer matrisi.
    int sifirSayisi; //Matriste kaç adet sıfır olduğunu tutan değişken.
    Scanner input;
    JTable table;
    JScrollPane scrollPane;
    DefaultTableModel model;
    
    Fasta(){
    super("Fasta"); //Frame başlığı.
    input = new Scanner(System.in);
    System.out.println("Sorgu sekansini giriniz:");
    sorgu = input.nextLine();
    System.out.println("Hedef sekansi giriniz:");
    hedef = input.nextLine();
    
    //Kullanıcıdan alınan sekansları matris haline getiren fonksiyon.
    matrisOlustur(); 
    
    //Tabloda header gizlenecek. 
    //Null olduğunda hata verdiği için tüm indisler "A" ile dolduruldu.
    sutunAdlari = new String[hedef.length()+1];
    for(int i = 0; i < hedef.length()+1 ; i++){
    sutunAdlari[i] = "A";
    }
    
    model = new DefaultTableModel(matris,sutunAdlari);
    table = new JTable(model);
    
    //Header gizlenir.
    table.setTableHeader(null); 
    
    //Tablodaki hücrelerin rengini değiştirmek için renderer ayarlandı.
    table.setDefaultRenderer(Object.class, new CellRenderer(diagonelBaslangic()));
    
    table.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
    scrollPane = new JScrollPane(table,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    this.add(scrollPane);
    this.setSize(1000,1000);
    this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    this.pack();
    this.setVisible(true);
    }
    
    private void matrisOlustur(){
    
    //Matrisin ilk hücresi boş olacağı için satır ve sütun sayıları
    //kullanıcıdan alınan sekans uzunluklarının bir fazlası olarak belirlendi.
    matris = new Object[sorgu.length()+1][hedef.length()+1];
    sifirSayisi = 0;
    
    //Sorgu sekansı ile hedef sekansının eşleşen dimerlerinin başlangıç
    //pozisyonlarına "*" işareti atıldı.
    for (int i = 0; i < sorgu.length()-1; i++){
        for (int y = 0; y < hedef.length()-1; y++)
            if(sorgu.substring(i,i+2).equals(hedef.substring(y, y+2)))               
                matris[i+1][y+1] = "*";
    }
    
    //Çapraz bulunan "*" işaretleri 0,1,2.. şeklinde numaralandırıldı.
    for (int i = 1; i < sorgu.length(); i++){
        for (int y = 1; y < hedef.length(); y++)
            if(matris[i][y] == "*"){
                //"*" işaretinin sol üst hücresi null ise "*" işareti sıfırdır.
                if(matris[i-1][y-1] == null){
                    matris[i][y] = 0;
                    sifirSayisi += 1;}
                
                //Sıfır değilse sol üst hücresindeki değerin bir fazlasıdır.
                else{
                    matris[i][y] = (int)matris[i-1][y-1]+1;}
               
                //Sağ alt hücresi null ise diagonelin son yıldızıdır.
                //Dimer matrisi olduğu için sağ alt hücre de numaralandırılır.
                if(matris[i+1][y+1] == null)
                    matris[i+1][y+1] = (int)matris[i][y] + 1;
            }         
    }
    
    //Sorgu sekansı matrise yerleştirildi.
    for (int i = 1; i < sorgu.length() + 1; i++){
        matris[i][0] = sorgu.charAt(i-1);           
    }
    
    //Hedef sekans matrise yerleştirildi.
    for (int y = 1; y < hedef.length() + 1; y++){
        matris[0][y] = hedef.charAt(y-1);           
    }
    }
    
    private int[][] diagonelBaslangic(){
    /*Matriste diagonellerin başlangıç noktası olan her bir 0 için 
    {Renk,Satır,Sütun} değerlerinin tutulacağı 2 boyutlu dizi. Bu değerler 
    kullanılarak CellRenderer'da aynı diagonel üzerindeki hücrelerin aynı 
    renkte olması sağlanacak.*/
    int[][] diagonel = new int[sifirSayisi][3];
    int x = 0;
    for (int i = 1; i < sorgu.length(); i++){
        for (int y = 1; y < hedef.length(); y++)
            if(matris[i][y] != null)
                if((int)table.getValueAt(i, y) == 0){
                    //Rastgele renk oluşturulur.
                    diagonel[x][0] = (int)(Math.random() * 0x1000000);
                    //0 noktasının satır değeri kaydedilir.
                    diagonel[x][1] = i;
                    //0 noktasının sütun değeri kaydedilir.
                    diagonel[x][2] = y;
                    x++;
                }
    
    }
    return diagonel;
    }

    public static void main(String[] args) {
        Fasta fasta = new Fasta();
    }    
}

class CellRenderer extends DefaultTableCellRenderer {

//diagonelBaslangic() fonksiyonuyla oluşturulan ve 0 noktalarının 
//{Renk, Satır, Sütun} bilgilerini tutan iki boyutlu dizi.
int[][] diagonel;

CellRenderer(int[][] diagonel){
this.diagonel = diagonel;
}

@Override
public Component getTableCellRendererComponent(
JTable table, Object value, boolean isSelected,
boolean hasFocus, int row, int column)
{
DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
Object deger = (Object) tableModel.getValueAt(row, column);

//Baz ve sayı içeren hücreler boyanır.
if (deger != null) {
    //Sorgu ve hedef sekansının bulunduğu hücreler boyanır.
    if (row == 0 || column == 0)
        setBackground(Color.white);
    
    /*Diagonel dizisindeki {Renk, Satır, Sütun} değerleri ile aynı diagonel
    üzerinde bulunan sayılar tespit edilir ve aynı renge boyanır. Eğer sayı 
    0 ise, satır ve sütun değerlerine bakılır. Eğer sayı, 0'dan farklı bir sayı 
    ise (satır-değer) ve (sütun-değer) değerlerine bakılır. Çünkü bu değerler 
    sayının, üzerinde bulunduğu diagonelin başlangıç noktası olan 0 noktasının 
    satır ve sütun değerlerini verir. 
    */
    
    //Kısacası her bir diagonel için 0 noktası
    //referans alındı ve renklendirmeler de bu referanslar üzerinden yapıldı
    else
        for (int[] x : diagonel) {
            if ((x[1] == row && x[2] == column)
                    || x[1] == (row-(int)deger) && x[2] == (column-(int)deger)){
               
                    setBackground(new Color(x[0]));}  
    }}
else{
setBackground(Color.white);
}
return super.getTableCellRendererComponent(table, value, isSelected, 
hasFocus, row, column);
}
}