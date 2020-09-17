package com.wf.ew.common.utils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/*import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;*/
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;


/**
 * Servlet implementation class TutableRead
 */
//@Component
public class OpenCvUtil  {

    //ftp保存目录(以“/”结束)
   /* @Value("${ftp.bastPath}")
    private String bPath;

    private final static String OPENCV_DLL_PATH = System.getProperty("user.dir")+"\\src\\main\\resources\\opencv\\opencv_java411.dll";

   static{
        System.load(OPENCV_DLL_PATH);
    }*/


    /**
     *
     */
   /* public  Map<String,String> getTableList(String filePath){
        System.out.println("OpenCV : " + Core.VERSION);
        filePath="E:/imgtest/tt.jpg";
        long startTime=System.currentTimeMillis();

        String basePath ="E:/imgtest/";
        *//*  String basePath =bPath+"/"+"opencvImage";
       File dir = new File(basePath);
        if(!dir.exists()) {
            dir.mkdirs();
        }*//*
        Mat src = Imgcodecs.imread( filePath);
        if(src.empty()){
            System.out.println( "not found file" );
            return null;
        }
        Mat gray = new Mat();
        Mat erod = new Mat();
        Mat blur = new Mat();
        int src_height=src.cols(), src_width=src.rows();
        //先转为灰度   cvtColor(src, gray, COLOR_BGR2GRAY);
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);

        *//**
         * 腐蚀（黑色区域变大）
         Mat element = getStructuringElement(MORPH_RECT, Size(erodeSize, erodeSize));
         erode(gray, erod, element);
         *//*
        int erodeSize = src_height / 200;
        if (erodeSize % 2 == 0){  erodeSize++; }
        Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(erodeSize, erodeSize));
        Imgproc.erode(gray, erod, element);

        //高斯模糊化
        int blurSize = src_height / 200;
        if (blurSize % 2 == 0) {blurSize++; }
        Imgproc.GaussianBlur(erod, blur,  new Size(blurSize, blurSize), 0, 0);


        //封装的二值化  adaptiveThreshold(~gray, thresh, 255, CV_ADAPTIVE_THRESH_MEAN_C, CV_THRESH_BINARY, 15, -2);
        Mat thresh = gray.clone();
        Mat xx = new Mat();
        Core.bitwise_not(gray,xx);//反色
        Imgproc.adaptiveThreshold(xx, thresh, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 15, -2);
		*//*
		这部分的思想是将线条从横纵的方向处理后抽取出来，再进行交叉，矩形的点，进而找到矩形区域的过程

		*//*
        // Create the images that will use to extract the horizonta and vertical lines
        //使用二值化后的图像来获取表格横纵的线
        Mat horizontal = thresh.clone();
        Mat vertical = thresh.clone();
        //这个值越大，检测到的直线越多
        String parameter ="10";
        int scale = Integer.parseInt(parameter); // play with this variable in order to increase/decrease the amount of lines to be detected  使用这个变量来增加/减少待检测的行数


        // Specify size on horizontal axis 指定水平轴上的大小
        int horizontalsize = horizontal.cols() / scale;
        // Create structure element for extracting horizontal lines through morphology operations 创建通过形态学运算提取水平线的结构元素
        // 为了获取横向的表格线，设置腐蚀和膨胀的操作区域为一个比较大的横向直条
        Mat horizontalStructure = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,new Size(horizontalsize, 1));
        // Apply morphology operations
        // 先腐蚀再膨胀
        // iterations 最后一个参数，迭代次数，越多，线越多。在页面清晰的情况下1次即可。
        Imgproc.erode(horizontal, horizontal, horizontalStructure,new Point(-1, -1),1 );
        Imgproc.dilate(horizontal, horizontal, horizontalStructure,new Point(-1, -1),1);
        // dilate(horizontal, horizontal, horizontalStructure, Point(-1, -1)); // expand horizontal lines

        // Specify size on vertical axis 同上
        int verticalsize = vertical.rows() / scale;
        // Create structure element for extracting vertical lines through morphology operations
        Mat verticalStructure = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,new Size(1, verticalsize));
        Imgproc.erode(vertical, vertical, verticalStructure,new Point(-1, -1),1);
        Imgproc.dilate(vertical, vertical, verticalStructure, new Point(-1, -1),1);
        *//*
         * 合并线条
         * 将垂直线，水平线合并为一张图
         *//*
        Mat mask = new Mat();
        Core.add(horizontal,vertical,mask);
        *//*
         * 通过 bitwise_and 定位横线、垂直线交汇的点
         *//*
        Mat joints=new Mat();
        Core.bitwise_and(horizontal, vertical, joints);
        *//*
         * 通过 findContours 找轮廓
         *
         * 第一个参数，是输入图像，图像的格式是8位单通道的图像，并且被解析为二值图像（即图中的所有非零像素之间都是相等的）。
         * 第二个参数，是一个 MatOfPoint 数组，在多数实际的操作中即是STL vectors的STL vector，这里将使用找到的轮廓的列表进行填充（即，这将是一个contours的vector,其中contours[i]表示一个特定的轮廓，这样，contours[i][j]将表示contour[i]的一个特定的端点）。
         * 第三个参数，hierarchy，这个参数可以指定，也可以不指定。如果指定的话，输出hierarchy，将会描述输出轮廓树的结构信息。0号元素表示下一个轮廓（同一层级）；1号元素表示前一个轮廓（同一层级）；2号元素表示第一个子轮廓（下一层级）；3号元素表示父轮廓（上一层级）
         * 第四个参数，轮廓的模式，将会告诉OpenCV你想用何种方式来对轮廓进行提取，有四个可选的值：
         *      CV_RETR_EXTERNAL （0）：表示只提取最外面的轮廓；
         *      CV_RETR_LIST （1）：表示提取所有轮廓并将其放入列表；
         *      CV_RETR_CCOMP （2）:表示提取所有轮廓并将组织成一个两层结构，其中顶层轮廓是外部轮廓，第二层轮廓是“洞”的轮廓；
         *      CV_RETR_TREE （3）：表示提取所有轮廓并组织成轮廓嵌套的完整层级结构。
         * 第五个参数，见识方法，即轮廓如何呈现的方法，有三种可选的方法：
         *      CV_CHAIN_APPROX_NONE （1）：将轮廓中的所有点的编码转换成点；
         *      CV_CHAIN_APPROX_SIMPLE （2）：压缩水平、垂直和对角直线段，仅保留它们的端点；
         *      CV_CHAIN_APPROX_TC89_L1  （3）or CV_CHAIN_APPROX_TC89_KCOS（4）：应用Teh-Chin链近似算法中的一种风格
         * 第六个参数，偏移，可选，如果是定，那么返回的轮廓中的所有点均作指定量的偏移
         *//*
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(mask,contours,hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE,new Point(0,0));

        List<MatOfPoint> contours_poly = contours;
        Rect[] boundRect = new Rect[contours.size()];
        List<Mat> tables = new ArrayList<Mat>();
        //my
        List<Rect> haveReacts = new ArrayList();
        Map<String, Map<String, Map<String, Double>>> mappoint=new HashMap<String, Map<String, Map<String, Double>>>();
        //循环所有找到的轮廓-点
        for(int i=0 ; i< contours.size(); i++){
            //每个表的点
            MatOfPoint point = contours.get(i);
            MatOfPoint contours_poly_point = contours_poly.get(i);
            *//*
             * 获取区域的面积
             * 第一个参数，InputArray contour：输入的点，一般是图像的轮廓点
             * 第二个参数，bool oriented = false:表示某一个方向上轮廓的的面积值，顺时针或者逆时针，一般选择默认false
             *//*
            double area = Imgproc.contourArea(contours.get(i));
            //如果小于某个值就忽略，代表是杂线不是表格
            if(area < 100){    continue;   }
            *//*
             * approxPolyDP 函数用来逼近区域成为一个形状，true值表示产生的区域为闭合区域。比如一个带点幅度的曲线，变成折线
             *
             * MatOfPoint2f curve：像素点的数组数据。
             * MatOfPoint2f approxCurve：输出像素点转换后数组数据。
             * double epsilon：判断点到相对应的line segment 的距离的阈值。（距离大于此阈值则舍弃，小于此阈值则保留，epsilon越小，折线的形状越“接近”曲线。）
             * bool closed：曲线是否闭合的标志位。
             *//*
            Imgproc.approxPolyDP(new MatOfPoint2f(point.toArray()),new MatOfPoint2f(contours_poly_point.toArray()),3,true);
            //为将这片区域转化为矩形，此矩形包含输入的形状
            boundRect[i] = Imgproc.boundingRect(contours_poly.get(i));
            // 找到交汇处的的表区域对象
            Mat table_image = joints.submat(boundRect[i]);

            List<MatOfPoint> table_contours = new ArrayList<MatOfPoint>();
            Mat joint_mat = new Mat();
            Imgproc.findContours(table_image, table_contours,joint_mat, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);
            //从表格的特性看，如果这片区域的点数小于4，那就代表没有一个完整的表格，忽略掉
            if (table_contours.size() < 4){  continue; }

            //表格里面的每个点
            Map<String, Double> x_zhis=new HashMap<String, Double>();
            Map<String, Double> y_zhis=new HashMap<String, Double>();
            for (MatOfPoint matOfPoint : table_contours) {
                Point[] array = matOfPoint.toArray();
                for (Point point2 : array) { x_zhis.put("x"+point2.x, point2.x); y_zhis.put("y"+point2.y, point2.y); }
            }
            //System.out.println( boundRect[i].x+"|"+boundRect[i].y+"|"+boundRect[i].width+"|"+boundRect[i].height+"|"+table_contours.size()+">>>>>>>>>>>>>>>>>>>");
            //my add
            haveReacts.add( boundRect[i]);
            Map<String, Map<String, Double>> x =new HashMap<String, Map<String,Double>>(); x.put("x", x_zhis);x.put("y", y_zhis);
            mappoint.put("key"+(haveReacts.size()-1),x   );

            //保存图片
            tables.add(src.submat(boundRect[i]).clone());
            //将矩形画在原图上
            Imgproc.rectangle(src, boundRect[i].tl(), boundRect[i].br(), new Scalar(255, 0, 255), 1, 8, 0);

        }

        //页面数据
        Map<String,String> jspdata=new HashMap<String, String>();

        for(int i=0; i< tables.size(); i++ ){
            Mat table = tables.get(i); Rect rect = haveReacts.get(i);
            int width = rect.width,height=rect.height;
            Map<String, Map<String, Double>> mapdata = mappoint.get("key"+i);
            int[] x_z = maptoint(mapdata.get("x"));
            int[] y_z = maptoint(mapdata.get("y"));

            //纵切
            String px_biao ="5";
            int x_len=0,x_biao=Integer.parseInt(px_biao);
            List<Mat> mats=new ArrayList<Mat>();
            for (int j = 0; j < x_z.length; j++) {
                if(j==0){
                    Mat img=new Mat(table,new Rect(0,0,x_z[j],height ));if(img.cols()>x_biao ){ mats.add(img); x_len++;}
                }else{
                    Mat img=new Mat(table,new Rect(x_z[j-1],0,x_z[j]-x_z[j-1],height )); if(img.cols()>x_biao ){mats.add(img);x_len++;}
                    if(j==x_z.length-1){//最后一个处理
                        Mat img1=new Mat(table,new Rect(x_z[x_z.length-1],0,width-x_z[x_z.length-1],height )); if(img.cols()>x_biao ){mats.add(img1); }
                    }
                }
            }
            imshow(basePath,table,"table_"+i+".png");//当前table图
            //横切保存
            String py_biao ="5";
            int y_len=0,y_biao=Integer.parseInt(py_biao );
            for (int j = 0; j <mats.size() ; j++) {   Mat mat = mats.get(j);
                int tuwidth = mat.cols(),tugao=mat.rows();
                int cy_len=0;
                for (int k = 0; k < y_z.length; k++) {
                    if(k==0){
                        Mat img=new Mat(mat,new Rect(0,0,tuwidth , y_z[k] ));if(img.rows()>y_biao ){ imshow(basePath, img,"table_"+i+"_"+j+"_"+cy_len+".png");     cy_len++; }
                    }else{
                        Mat img=new Mat(mat,new Rect(0,y_z[k-1],tuwidth,y_z[k]-y_z[k-1]));if(img.rows()>y_biao ){  imshow(basePath, img,"table_"+i+"_"+j+"_"+cy_len+".png");  cy_len++;}
                        if(k==y_z.length-1){//最后一个处理
                            Mat img1=new Mat(mat,new Rect(0,y_z[k],tuwidth,tugao-y_z[k] ));if(img.rows()>y_biao ){ imshow(basePath, img1,"table_"+i+"_"+j+"_"+(cy_len)+".png");   }
                        }
                    }
                }
                y_len=cy_len;
            }
            //保存数据信息
            jspdata.put("table_"+i, x_len+"_"+y_len);
        }


        Map<String,String> jspdata1=new HashMap<String, String>();
        int num=0;
        for (Map.Entry<String, String> d : jspdata.entrySet()) {
            String value= d.getValue();
            if(value.indexOf("_")!=-1){
                //
                String x="";
                String len[]=value.split("_");
                int xlen=Integer.parseInt(len[0]);int ylen=Integer.parseInt(len[1]);
                for(int i=0;i<ylen;i++){
                    //行
                    for(int j=0;j<xlen;j++){
                        String name="table_"+num+"_"+j+"_"+i+".png";
                         String path = basePath+name;

                        String text="";//---------tess4j识别文字-------------
                        try {
                           File file = new File(path);
                            ITesseract instance = new Tesseract();

                            //设置训练库的位置
                            File file2 = null;
                            try {
                                file2 = ResourceUtils.getFile("classpath:tessdata");
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            instance.setDatapath(file2.getPath());
                            //instance.setDatapath(lagnguagePath);

                            //chi_sim ：简体中文， eng    根据需求选择语言库
                            instance.setLanguage("chi_sim");
                            try {
                                text =  instance.doOCR(file);
                            } catch (TesseractException e) {
                                e.printStackTrace();
                            }

                        } catch (Exception e) { System.out.println("读取错误");	}
                        try {
                            Thread.sleep(400);//百度qps限制
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        x+=(text.equalsIgnoreCase("")?" ":text)+"&_&";
                    }
                    x=x.substring(0, x.lastIndexOf("&_&"));
                    x+="#_#";
                }
                //
                jspdata1.put("shibie"+num, x);
            }
            num++;
        }
        long endTime=System.currentTimeMillis();
        System.out.println("time:"+ (float)(endTime-startTime)/1000);
        System.out.println(jspdata1);
        return jspdata1;
    }

    public void imshow(String basePath,Mat dst,String name)  {
        Imgcodecs.imwrite(basePath+"/"+name, dst);
    }

    public String getjsontext(JSONArray array){
        String text="";
        for (int i = 0; i < array.size(); i++) { JSONObject textx = (JSONObject)array.get(i); text+=textx.get("words");    }
        return text;
    }
    public int[] maptoint(Map<String, Double> x) {
        int[] zhi=new int[x.size()];int num=0;
        for (Map.Entry<String, Double> m :x.entrySet())  {
            zhi[num]=m.getValue().intValue(); num++;
        }
        Arrays.sort(zhi);
        return zhi;
    }*/

}