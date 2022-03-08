import java.io.*;
import java.util.*;
import java.nio.file.*;
import java.time.*;
import java.nio.file.attribute.FileTime;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

//import java.util.Properties;
//import javax.mail.*;
//import javax.mail.internet.*;
//import javax.mail.MessagingException; //mail 관련 library


public class Backup_Delete {

    static int remainTime = 0;
    static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    public static void scanDirectory(String folderPath, List<String> fileLst) {
        File[] files = new File(folderPath).listFiles();
        for(File f : files) {
            if(f.isDirectory()) {
                scanDirectory(f.getAbsolutePath(), fileLst);
            }
            else {
                fileLst.add(f.getAbsolutePath());
            }
        }
    }

    public static String getCurrDate() {
        LocalDateTime now = LocalDateTime.now();
        return (now.toString().substring(0,10)); //현재 시간에서 날짜 추출
    }

    public static String fileCurrDate(String file_name) throws IOException{
        Path file = Paths.get(file_name);
        FileTime creationTime = (FileTime) Files.getAttribute(file, "creationTime"); // 결과 출력
        LocalDateTime ldt =  LocalDateTime.ofInstant( creationTime.toInstant(), ZoneId.systemDefault());

        return ldt.toString().substring(0,10);
    }

    public static boolean isFileToday(String currDate, String fileName) throws IOException{
        if (currDate.equals(fileCurrDate(fileName))){
            return true;
        }
        else return false;
    }

    public static void outRemainTime(int second){
        int hour = second / 3660;
        int min = (second % 3660) / 60;
        int sec = second % 60;

        System.out.println("삭제까지 남은 시간: " + hour + "시간 " + min + "분 " + sec + "초");
        System.out.println(LocalDateTime.now().toString());
    }

    public static void fileDelete(List<String> fileList) throws IOException{
        String currDate = getCurrDate();
        int deleteCheck = 1;

        for (String fileName : fileList) {//오늘 만들어진 것이 아닌 파일
                if(!isFileToday(currDate,fileName)) {
                    File del_file = new File(fileName);
                    del_file.delete();
                    System.out.println(fileName + " is deleted!");
                    deleteCheck = 0;
                }
        }
        if(deleteCheck == 1){
            System.out.println("No file deleted!");
        }
    }

    public static String inputFolderPath() throws IOException{
        System.out.print("파일 경로를 입력하세요: ");
        return br.readLine();
    }

    public static int reserveTime() throws IOException{
        System.out.print("삭제할 시간을 입력하세요(ex. 0930, 2359): ");
        return delayTime(br.readLine());
    }

    public static int delayTime(String DeleteTime){
        LocalDateTime now = LocalDateTime.now();

        int hour = Integer.parseInt(DeleteTime.substring(0,2));
        int minute = Integer.parseInt(DeleteTime.substring(2,4));
        LocalDateTime requestTime = LocalDateTime.of(now.toLocalDate(),LocalTime.of(hour, minute));

        Duration whenDelete = Duration.between(now, requestTime);

        //System.out.println(duration.getSeconds()); //확인용
        if(whenDelete.getSeconds() <= 0){
            whenDelete = whenDelete.plusMinutes(1);
        }
        //System.out.println(duration.getSeconds());
        return (int)whenDelete.getSeconds();
    }


    public static void main(String args[]) throws IOException{
        final ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1); // 주기적인 작업

        String folderRoute = inputFolderPath();
        remainTime = reserveTime();

        exec.scheduleAtFixedRate(new Runnable(){
            public void run(){
                try {

                    if(remainTime>0) {    //삭제까지 남은 시간이 0보다 크면
                        outRemainTime(remainTime--);
                    }

                    else{
                        remainTime = 9; // 삭제 주기 (하루: 85,399)
                        List<String> fileList = new ArrayList<String>();
                        scanDirectory(folderRoute, fileList);//확인
                        fileDelete(fileList);
                    }

                } catch (Exception e) {
                    e.printStackTrace(); // 에러 발생시 Executor를 중지시킨다
                    exec.shutdown();
                }
            }
        }, 0 , 1, TimeUnit.SECONDS);

    }
}