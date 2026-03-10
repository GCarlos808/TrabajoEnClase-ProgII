package trabajoenclase_progii;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Calendar;
import java.util.Date;


public class EmpleadosManager {

    /*
    Formato:
    1.File Codigos.emp:
    int code -> 4 bytes Mantener
    2.File empleados.emp:
    int code
    String name;
    double salary
    long fechaContratacion
    long fechaDespido
    */
    
    private RandomAccessFile rcods, remps;
    
    public EmpleadosManager(){
    
        try{
        File mf= new File("company");
        mf.mkdir();
        
        
        rcods = new RandomAccessFile("company/codigos.emp", "rw");    
        remps = new RandomAccessFile("company/empleados.emp", "rw");
        
        initCode();
        
        } catch (IOException e){
            System.out.println("Error");
        }
        
    }
    
    private void initCode() throws IOException{
    
        if (rcods.length() == 0)
            rcods.writeInt(1);
    }
    
    private int getCode() throws IOException {
        
        rcods.seek(0);
        
        int code = rcods.readInt();
        
        rcods.seek(0);
        rcods.writeInt(code+1);
        
        return code;
    }
    
    public void addEmployee(String name, double salary) throws IOException {
        remps.seek(remps.length());
        int code = getCode();
        remps.writeInt(code);
        remps.writeUTF(name);
        remps.writeDouble(salary);
        remps.writeLong(Calendar.getInstance().getTimeInMillis());
        remps.writeLong(0);
        
        //Folder
        
        createEmployeeFolders(code);
        
    }
    private String employeeFolder(int code){
        return "company/empleado"+code;
    }
    
    private RandomAccessFile salesFileFor(int code) throws IOException{
        
        String dirPadre = employeeFolder(code);
        int year = Calendar.getInstance().get(Calendar.YEAR);
        
        String dir = dirPadre+ "/ventas" +year +".emp";
        
        return new RandomAccessFile (dir, "rw");
    }
    
    /*
    Formato VentasYear.emp
    double saldo
    boolean estadoPago
    */
    
    private void createYearSalesFile(int code) throws IOException {
        
        RandomAccessFile rventa = salesFileFor(code);
        
        if (rventa.length() == 0) {
            for (int i = 0; i < 12; i++) {
                rventa.writeDouble(0);
                rventa.writeBoolean(false);
            }
        }
        
    }
    
    private void createEmployeeFolders(int code) throws IOException {
        
        File vf= new File(employeeFolder(code));
        vf.mkdir();
        createYearSalesFile(code);
        
    }
    
    public void employeeList() throws IOException{
        
        remps.seek(0);
        
        while (remps.getFilePointer() < remps.length()){
            
            int code = remps.readInt();
            String name = remps.readUTF();
            double salary = remps.readDouble();
            Date dateH = new Date(remps.readLong());
            
            
            if (remps.readLong() == 0) {
                System.out.println(code +"  -  " +name+ "  -  "+ salary+ "$ - " +dateH);
            }
        }
    }
    
    public RandomAccessFile billsFileFor(int code) throws IOException{
                
        File nf = new File("empleados" + code);
        if (!nf.exists()) 
            nf.mkdirs();

        File file = new File(nf, "recibos.emp");
        return new RandomAccessFile(file, "rw");
    
        
    }
    
    public boolean isEmployedPayed(int code) throws IOException{
        RandomAccessFile sales = salesFileFor(code);
        
        int month = Calendar.getInstance().get(Calendar.MONTH);
        long pos = month * 9;
        
        sales.seek(pos + 8);
        return sales.readBoolean();
    }
    
    
}