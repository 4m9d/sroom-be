package com.m9d.sroom.component;

import lombok.extern.slf4j.Slf4j;
import com.m9d.sroom.util.DateUtil;

import java.util.Date;

/**
 * BaseBatch
 *
 */
@Slf4j
public class BaseBatch {

	/**
	 * 배치 이름
	 */
	protected String batchName;

	/**
	 * 배치 시작 시간
	 */
	protected Date startedAt;

	/**
	 * 배치 시작 시간 로그를 남긴다.
	 */
	protected void logStartedAt() {
		startedAt = new Date();
		log.info("{} has been started; {}", batchName, startedAt);
	}

	/**
	 * 배치 종료 시간 로그를 남긴다.
	 */
	protected void logEndedAt() {
		DateUtil dateUtil = new DateUtil();
		dateUtil.measureTime(batchName, startedAt);
	}

}
