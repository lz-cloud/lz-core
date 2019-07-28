package com.wkclz.core.util;

import com.wkclz.core.pojo.entity.AreaEntity;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AreaUtil {

    private static final Logger logger = LoggerFactory.getLogger(AreaUtil.class);

    private static final String BASE_URL = "http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2018/";
    private static int RETRY_TIMES = 0;

    private static final String TEMP_FILE = System.getProperty("user.dir") + "/logs/areas/";

    public static void main(String[] args) {
        List<AreaEntity> areas = getAreas();
        for (AreaEntity area : areas) {
            System.out.println(area);
        }
    }

    public static List<AreaEntity> getAreas() {
        List<AreaEntity> areas = new ArrayList<>();
        getProvinces(BASE_URL + "index.html", areas, 0L);
        return areas;
    }

    /**
     * 获取所有省
     *
     * @param url
     * @param areas
     */
    private static void getProvinces(String url, List<AreaEntity> areas, Long parentCode) {
        Document provinceDoc = getDoc(url);
        if (provinceDoc == null){
            return;
        }
        Elements mastheads = provinceDoc.select("tr.provincetr");
        for (Element masthead : mastheads) {
            Elements resultLinks = masthead.select("td > a");
            for (Element ddd : resultLinks) {
                // 省
                String href = ddd.attr("href");
                String name = ddd.text();
                String code = href.substring(0, 2) + "0000000000";

                AreaEntity area = new AreaEntity();
                area.setParentAreaCode(parentCode);
                area.setLevel(1);
                area.setIsLeaf(0);
                Long areaCode = Long.valueOf(code);
                area.setAreaCode(areaCode);
                area.setName(name);
                areas.add(area);

                // 市 url
                String cityUrl = BASE_URL + href;
                System.out.println("cityUrl: " + cityUrl);
                getCitys(cityUrl, areas, areaCode);
            }
        }

    }

    /**
     * 获取所有市
     *
     * @param url
     * @param areas
     */
    private static void getCitys(String url, List<AreaEntity> areas, Long parentCode) {
        Document cityDoc = getDoc(url);

        if (cityDoc == null){
            return;
        }
        url = url.substring(0, url.lastIndexOf("."));
        url = url.substring(0, url.lastIndexOf("/"));
        url = url + "/";

        Elements mastheads = cityDoc.select("tr.citytr");
        for (Element masthead : mastheads) {

            Elements tds = masthead.select("td");
            String code = tds.get(0).text();
            String name = tds.get(1).text();

            AreaEntity area = new AreaEntity();
            area.setParentAreaCode(parentCode);
            area.setLevel(2);
            area.setIsLeaf(0);
            Long areaCode = Long.valueOf(code);
            area.setAreaCode(areaCode);
            area.setName(name);
            areas.add(area);

            // 区/县 url
            Elements a1 = tds.get(0).select("a");
            if (a1 == null) {
                area.setIsLeaf(1);
                continue;
            }
            String current = a1.get(0).attr("href");
            if (StringUtils.isBlank(current)) {
                area.setIsLeaf(1);
                continue;
            }
            String countyUrl = url + current;
            System.out.println("countyUrl: " + countyUrl);
            getCountys(countyUrl, areas, areaCode);
        }
    }


    /**
     * 获取所有县
     *
     * @param url
     * @param areas
     */
    private static void getCountys(String url, List<AreaEntity> areas, Long parentCode) {
        Document countryDoc = getDoc(url);

        if (countryDoc == null){
            return;
        }
        url = url.substring(0, url.lastIndexOf("."));
        url = url.substring(0, url.lastIndexOf("/"));
        url = url + "/";

        Elements mastheads = countryDoc.select("tr.countytr");
        for (Element masthead : mastheads) {

            Elements tds = masthead.select("td");
            String code = tds.get(0).text();
            String name = tds.get(1).text();

            AreaEntity area = new AreaEntity();
            area.setParentAreaCode(parentCode);
            area.setLevel(3);
            area.setIsLeaf(0);
            Long areaCode = Long.valueOf(code);
            area.setAreaCode(areaCode);
            area.setName(name);
            areas.add(area);

            // 乡镇 url
            Elements a1 = tds.get(0).select("a");
            if (a1 == null || a1.size() == 0) {
                area.setIsLeaf(1);
                continue;
            }
            String current = a1.get(0).attr("href");
            if (StringUtils.isBlank(current)) {
                area.setIsLeaf(1);
                continue;
            }

            String townUrl = url + current;
            System.out.println("townUrl: " + townUrl);
            getTowns(townUrl, areas, areaCode);
        }
    }


    /**
     * 获取所有乡镇
     *
     * @param url
     * @param areas
     */
    private static void getTowns(String url, List<AreaEntity> areas, Long parentCode) {
        Document countryDoc = getDoc(url);
        if (countryDoc == null){
            return;
        }
        url = url.substring(0, url.lastIndexOf("."));
        url = url.substring(0, url.lastIndexOf("/"));
        url = url + "/";

        Elements mastheads = countryDoc.select("tr.towntr");
        for (Element masthead : mastheads) {

            Elements tds = masthead.select("td");
            String code = tds.get(0).text();
            String name = tds.get(1).text();

            AreaEntity area = new AreaEntity();
            area.setParentAreaCode(parentCode);
            area.setLevel(4);
            area.setIsLeaf(0);
            Long areaCode = Long.valueOf(code);
            area.setAreaCode(areaCode);
            area.setName(name);
            areas.add(area);

            // 乡镇 url
            Elements a1 = tds.get(0).select("a");
            if (a1 == null) {
                area.setIsLeaf(1);
                continue;
            }
            String current = a1.get(0).attr("href");
            if (StringUtils.isBlank(current)) {
                area.setIsLeaf(1);
                continue;
            }

            String villagetrUrl = url + current;
            System.out.println("villagetrUrl: " + villagetrUrl);
            getVillagetrs(villagetrUrl, areas, areaCode);
        }
    }


    /**
     * 获取所有村/居委会/街道
     *
     * @param url
     * @param areas
     */
    private static void getVillagetrs(String url, List<AreaEntity> areas, Long parentCode) {
        Document countryDoc = getDoc(url);
        if (countryDoc == null){
            return;
        }
        Elements mastheads = countryDoc.select("tr.villagetr");
        for (Element masthead : mastheads) {

            Elements tds = masthead.select("td");
            String code = tds.get(0).text();
            String typeCode = tds.get(1).text();
            String name = tds.get(2).text();

            AreaEntity area = new AreaEntity();
            area.setParentAreaCode(parentCode);
            area.setLevel(5);
            area.setAreaCode(Long.valueOf(code));
            area.setTypeCode(Integer.valueOf(typeCode));
            area.setName(name);
            area.setIsLeaf(1);
            areas.add(area);
        }
    }


    /**
     * 从指定 Url 获取文档
     *
     * @param urlStr
     * @return
     */
    private static Document getDoc(String urlStr) {

        // 本地缓存
        File baseFile = new File(TEMP_FILE);
        if (!baseFile.exists()) {
            baseFile.mkdirs();
        }
        // http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2017/13/07/30/130730110.html
        String xPath = urlStr.substring(urlStr.indexOf("/tjyqhdmhcxhfdm/") + "/tjyqhdmhcxhfdm/".length());
        xPath = xPath.substring(0, xPath.lastIndexOf("/"));
        File savePath = new File(TEMP_FILE + xPath);
        if (!savePath.exists()) {
            savePath.mkdirs();
        }
        File file2Save = new File(savePath + urlStr.substring(urlStr.lastIndexOf("/")));

        InputStream inputStream = null;
        FileOutputStream fos = null;
        OutputStreamWriter osw = null;
        try {
            Document doc;

            if (file2Save.exists()) {
                doc = Jsoup.parse(file2Save, "GB2312", urlStr);
            } else {

                inputStream = new URL(urlStr).openStream();
                doc = Jsoup.parse(inputStream, "GB2312", urlStr);

                String html = doc.html();

                fos = new FileOutputStream(file2Save, false);
                osw = new OutputStreamWriter(fos, "gb2312");
                osw.write(html);
                osw.flush();
            }

            RETRY_TIMES = 0;
            return doc;
        } catch (IOException e) {
            if (RETRY_TIMES++ < 5) {
                System.out.println("失败尝试次数：" + RETRY_TIMES + ", 地址为： " + urlStr);
                return getDoc(urlStr);
            }
            logger.error("JsonProcessingException", e);
        } finally {
            if (inputStream != null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    logger.error("IOException", e);
                }
            }
            if (fos != null){
                try {
                    fos.close();
                } catch (IOException e) {
                    logger.error("IOException", e);
                }
            }
            if (osw != null){
                try {
                    osw.close();
                } catch (IOException e) {
                    logger.error("IOException", e);
                }
            }
        }
        return null;
    }

    ;
}
