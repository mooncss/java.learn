package com.example.mall.maputil;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class MapUtil
{
    /**
     * 判断是否在多边形区域内
     *
     * @param pointLon
     *            要判断的点的纵坐标
     * @param pointLat
     *            要判断的点的横坐标
     * @param lon
     *            区域各顶点的纵坐标数组
     * @param lat
     *            区域各顶点的横坐标数组
     * @return
     */
    public static boolean isInPolygon(double pointLon, double pointLat, double[] lon,
                                      double[] lat) {
        // 将要判断的横纵坐标组成一个点
        Point2D.Double point = new Point2D.Double(pointLon, pointLat);
        // 将区域各顶点的横纵坐标放到一个点集合里面
        List<Point2D.Double> pointList = new ArrayList<Point2D.Double>();
        double polygonPoint_x = 0.0, polygonPoint_y = 0.0;
        for (int i = 0; i < lon.length; i++) {
            polygonPoint_x = lon[i];
            polygonPoint_y = lat[i];
            Point2D.Double polygonPoint = new Point2D.Double(polygonPoint_x, polygonPoint_y);
            pointList.add(polygonPoint);
        }
        return check(point, pointList);
    }

//    public static void main(String[] args) {
//        double [] lon =new double[]{113.9878320694,114.0037536621,114.0850353241,114.0858078003};
//        double [] lat=new double[]{33.5978917984,33.5458299577,33.5529116402,33.6082575083};
//
//       double pointlon = 113.9798927307;
//       double pointLat = 33.5826626685;
//
//        System.out.println(isInPolygon(pointlon,pointLat,lon,lat));
//    }

    /**
     * 一个点是否在多边形内
     *
     * @param point
     *            要判断的点的横纵坐标
     * @param polygon
     *            组成的顶点坐标集合
     * @return
     */
    private static boolean check(Point2D.Double point, List<Point2D.Double> polygon) {
        java.awt.geom.GeneralPath peneralPath = new java.awt.geom.GeneralPath();

        Point2D.Double first = polygon.get(0);
        // 通过移动到指定坐标（以双精度指定），将一个点添加到路径中
        peneralPath.moveTo(first.x, first.y);
        polygon.remove(0);
        for (Point2D.Double d : polygon) {
            // 通过绘制一条从当前坐标到新指定坐标（以双精度指定）的直线，将一个点添加到路径中。
            peneralPath.lineTo(d.x, d.y);
        }
        // 将几何多边形封闭
        peneralPath.lineTo(first.x, first.y);
        peneralPath.closePath();
        // 测试指定的 Point2D 是否在 Shape 的边界内。
        return peneralPath.contains(point);
    }
}
