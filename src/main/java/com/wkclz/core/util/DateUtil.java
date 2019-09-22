package com.wkclz.core.util;


import com.wkclz.core.base.BaseModel;
import com.wkclz.core.pojo.enums.DateRangeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Description:
 * Created: wangkaicun @ 2017-10-18 下午10:21
 */
public class DateUtil {

    private static final Logger logger = LoggerFactory.getLogger(DateUtil.class);

    private static final String SDF_YYYY_MM_DD = "yyyy-MM-dd";
    private static final String SDF_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    private static final String SDF_YYYY_MM_DD_HH_MM_SS_SSS = "yyyy-MM-dd HH:mm:ss.SSS";


    private final static long DAY = 24 * 60 * 60 * 1000L;
    private final static long HOUR = 60 * 60 * 1000L;
    private final static long MIN = 60 * 1000L;
    private final static long SEC = 1000L;


    /**
     * yyyy-MM-dd
     *
     * @param date
     * @return
     */
    public static final String getYyyyMmDd(Date date) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat sdfYms = new SimpleDateFormat(SDF_YYYY_MM_DD);
        return sdfYms.format(date);
    }


    /**
     * yyyy-MM-dd
     *
     * @param date
     * @return
     */
    public static final String getYyyyMmDd(Long date) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat sdfYms = new SimpleDateFormat(SDF_YYYY_MM_DD);
        return sdfYms.format(date);
    }


    /**
     * yyyy-MM-dd HH:mm:ss
     *
     * @param date
     * @return
     */
    public static final String getYyyyMmDdHhMmSs(Date date) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat sdfYmdhms = new SimpleDateFormat(SDF_YYYY_MM_DD_HH_MM_SS);
        return sdfYmdhms.format(date);
    }


    /**
     * yyyy-MM-dd HH:mm:ss
     *
     * @param date
     * @return
     */
    public static final String getYyyyMmDdHhMmSs(Long date) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat sdfYmdhms = new SimpleDateFormat(SDF_YYYY_MM_DD_HH_MM_SS);
        return sdfYmdhms.format(date);
    }


    /**
     * yyyy-MM-dd HH:mm:ss.SSS
     *
     * @param date
     * @return
     */
    public static final String getYyyyMmDdHhMmSsSss(Date date) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat sdfYmdhmss = new SimpleDateFormat(SDF_YYYY_MM_DD_HH_MM_SS_SSS);
        return sdfYmdhmss.format(date);
    }


    /**
     * yyyy-MM-dd HH:mm:ss.SSS
     *
     * @param date
     * @return
     */
    public static final String getYyyyMmDdHhMmSsSss(Long date) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat sdfYmdhmss = new SimpleDateFormat(SDF_YYYY_MM_DD_HH_MM_SS_SSS);
        return sdfYmdhmss.format(date);
    }

    /**
     * 字符串格式化为时间
     *
     * @param dateStr
     * @return
     */
    public static final Date getDate(String dateStr) {
        if (dateStr == null) {
            return null;
        }
        if (dateStr.length() == SDF_YYYY_MM_DD.length()) {
            dateStr += " 00:00:00";
        }
        if (dateStr.length() == SDF_YYYY_MM_DD_HH_MM_SS.length()) {
            SimpleDateFormat sdfYmdhms = new SimpleDateFormat(SDF_YYYY_MM_DD_HH_MM_SS);
            try {
                Date date = sdfYmdhms.parse(dateStr);
                return date;
            } catch (ParseException e) {
                logger.error("ParseException", e);
            }
        }
        throw new RuntimeException("Error DateTime string");
    }

    /**
     * 时间范围处理。枚举转时间范围
     *
     * @param dto
     */
    public static void formatDateRange(BaseModel dto) {
        DateRangeType type = dto.getDateRangeType();
        if (type == null) {
            return;
        }
        Date timeTo = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(getDayBegin());
        if (type == DateRangeType.HOUR) {
            c.add(Calendar.HOUR_OF_DAY, -1);
        }
        if (type == DateRangeType.YESTERDAY) {
            c.add(Calendar.DATE, -1);
        }
        if (type == DateRangeType.WEEK) {
            c.add(Calendar.DATE, -7);
        }
        if (type == DateRangeType.MONTH) {
            c.add(Calendar.MONTH, -1);
        }
        if (type == DateRangeType.QUATER) {
            c.add(Calendar.MONTH, -3);
        }
        if (type == DateRangeType.YEAR) {
            c.add(Calendar.YEAR, -1);
        }
        Date timeFrom = c.getTime();
        dto.setTimeFrom(timeFrom);
        dto.setTimeTo(timeTo);
    }


    // 获取今天 0 时
    public static Date getDayBegin() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }


    /**
     * 计算历史到两套上的时间，转换为直观的文字描述
     * @param history
     * @return
     */
    public static String getTimeDifference(Date history){
        return getTimeDifference(history, null);
    }

    /**
     * 计算历史到两套上的时间，转换为直观的文字描述
     * @param history
     * @param future
     * @return
     */
    public static String getTimeDifference(Date history, Date future){
        long now = System.currentTimeMillis();

        long his = now;
        long fut = now;

        if (history != null){
            his = history.getTime();
        }
        if (future != null){
            fut = future.getTime();
        }

        long timeLess = fut - his;

        long day = timeLess / DAY;
        timeLess = timeLess % DAY;

        long hour = timeLess / HOUR;
        timeLess = timeLess % HOUR;

        long min = timeLess / MIN;
        timeLess = timeLess % MIN;

        long sec = timeLess / SEC;

        StringBuffer sb = new StringBuffer();
        if (day > 0){
            sb.append(day + "天 ");
        }
        if (day > 0 || hour > 0){
            sb.append(hour + "时 ");
        }
        if (day > 0 || hour > 0 || min > 0){
            sb.append(min + "分 ");
        }
        if (day > 0 || hour > 0 || min > 0 || sec > 0){
            sb.append(sec + "秒 ");
        }
        if (sb.length() == 0){
            sb.append("0秒");
        }
        return sb.toString();
    }



}
