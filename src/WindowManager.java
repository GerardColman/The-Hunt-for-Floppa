/*
Here I will put logic to switch windows/levels
 */

public class WindowManager {
    public static void main(String[] args) {
        try{
            MainWindow hello = new MainWindow();  //sets up environment
            hello.run();
        }catch(Exception e){
            e.printStackTrace();
        }

    }
}
