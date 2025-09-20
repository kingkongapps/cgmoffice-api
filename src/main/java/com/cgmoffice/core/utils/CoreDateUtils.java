package com.cgmoffice.core.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.cgmoffice.core.exception.CmmnBizException;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class CoreDateUtils {


	public String getYyyyMMddHHmmssSSS(){
		long dateTime = System.currentTimeMillis();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.KOREA);
		return sdf.format(dateTime);
	}

	/**
	 * timestamp 값을 format 에 해당하는 string 형식의 날자값으로 변환해주는 함수
	 * @param timestamp
	 * @param format "yy-MM-dd HH:mm:ss"
	 * @return
	 */
	public String timestamp2Date(long timestamp, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat( format , Locale.KOREA );
		return sdf.format( new Date( timestamp ) );
	}

	/**
	 * 오늘날자를 기준으로 입된 날자와의 차이를 일단로 리턴
	 * @param targetDate yyyyMMdd
	 * @return
	 */
	public int diff(String targetDate) {
		String DATE_PATTERN = "yyyyMMdd";
        int MILLI_SECONDS_PER_DAY = 24 * 60 * 60 * 1000;
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
        Date today = new Date();
        Date endDateDate;
		try {
			endDateDate = sdf.parse(targetDate);

			long rslt = (today.getTime() - endDateDate.getTime());
			if(rslt < 0) {
				rslt = rslt - MILLI_SECONDS_PER_DAY;
			}

			return (int) (rslt / MILLI_SECONDS_PER_DAY);
		} catch (ParseException e) {
			return 9999;
		}

	}

	/**
	 * 기준날자의 요일 가지고 오기
	 * @param date yyyyMMdd 형식으로 입력
	 * @return
	 */
	public String getDayOfWeekForDate(String date) {
		Calendar cal = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        try {
            cal.setTime(df.parse(date));
        } catch (ParseException e) {
            log.error(CoreUtils.getExceptionStackTrace(e));
            return null;
        }
        return cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.KOREAN);
	}

	/**
	 * 날자 계산 함수
	 * @param strDate yyyyMMdd
	 * @param year 계산할 년수
	 * @param month 계산할 월수
	 * @param day 계산할 일수
	 * @return
	 */
	public String calcDate(String strDate, int year, int month, int day) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		Calendar cal = Calendar.getInstance();
		Date dt;
		try {
			dt = dateFormat.parse(strDate);
		} catch (ParseException e) {
			log.error(CoreUtils.getExceptionStackTrace(e));
			return null;
		}

		cal.setTime(dt);

		cal.add(Calendar.YEAR, year);
		cal.add(Calendar.MONTH, month);
		cal.add(Calendar.DATE, day);

		return dateFormat.format(cal.getTime());
	}

	/**
	 * 입력된 날자의 요일번호 가지고 오기 (1:일요일 ~ 7:토요일)
	 *
	 * @param yyyyMMdd
	 * @return 1:일요일 ~ 7:토요일
	 */
	public int getDayOfWeekNumber(String yyyyMMdd) {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        Calendar calendar = Calendar.getInstance();
        try {
			calendar.setTime(df.parse(yyyyMMdd));
		} catch (ParseException e) {
			log.error(CoreUtils.getExceptionStackTrace(e));
			throw new CmmnBizException("일자 파싱에러!!!");
		}
        return calendar.get(Calendar.DAY_OF_WEEK);  // 1:일요일 ~ 7:토요일
	}

	/**
	 * Date객체를 pattern 형식으로 변환하여 반환
	 * @param date Date객체
	 * @param pattern 패턴(예 : "yyyy-MM-dd HH:mm:ss:SSS")
	 * @return
	 */
	public String getDateFormedString(Date date, String pattern) {
		SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.getDefault());
		return format.format(date);
	}
}
