package com.ll.download;

public class Main {
    public static void main(String[] args) {
        DownloadTask downloadTask = new DownloadTask("http://uploadpla.net/files/5106_fnuhx/EmpireEFI_V108_all-in-one.zip", Const.DOWNLOAD_DIR,null);
        downloadTask.download();
//        new Thread(new DownloadThreadRunner("http://dldx.csdn.net/fd.php?i=393843408919813&s=cda783d1ab8b2cc2fe956e66bcebf0b6", Const.DOWNLOAD_DIR+"/test.file")).start();
    }
}
