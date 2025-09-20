package com.cgmoffice.core.scheduler;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Throwables;
import com.cgmoffice.api.cnt.dto.PrdtClusDto;
import com.cgmoffice.core.dao.AppDao;
import com.cgmoffice.core.dao.ModuleDao;
import com.cgmoffice.core.utils.CoreDateUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScheduledTasks {
	private final AppDao appDao;
	private final ModuleDao moduleDao;

	@Value("${cmmn-properties.makeDbBackup.dbTableList}")
	private String dbTableList;

	@Value("${cmmn-properties.makeDbBackup.ext}")
	private String ext;

	@Value("${cmmn-properties.makeDbBackup.backupFileDir}")
	private String backupFileDir;

	//현재 디렉토리 경로 (현재 = C:\STS3\bin\sts-3.9.11)
	private String fileProperty = System.getProperty("user.dir");

//    // 5분마다 실행 [로컬에서만 활용 할 것]
//    @Scheduled(fixedRate = 30000)
//    public void runEvery300Seconds() {
//    	this.cron01();
//    	this.makeDbBackupFiles();
//    }

//    // 이전 작업이 끝나고 5초 후 실행
//    @Scheduled(fixedDelay = 5000)
//    public void runAfterDelay() {
//        System.out.println("fixedDelay - 작업 종료 후 5초 뒤 실행: " + LocalDateTime.now());
//    }

//    // 매일 오전 1시 정각에 실행
//    @Scheduled(cron = "0 0 1 * * *")
//    public void runEveryDayAt1AM() {
//        System.out.println("cron - 매일 오전 1시 실행: " + LocalDateTime.now());
//    }

//    @Scheduled(cron = "${cmmn-properties.schedule-setting.cron01}")
//    public void runCron01() {
//        System.out.println("runCron01: " + LocalDateTime.now());
//    }

//    @Scheduled(cron = "#{@cmmnProperties.scheduleSetting.cron01}")
//    public void runCron01() {
//        System.out.println(">>> runCron01: " + LocalDateTime.now());
//    }

	//db 파일 백업 데이터 생성 [매일 정각 마다 자동 생성]
	@Scheduled(cron = "${cmmn-properties.schedule-setting.makeDbBackupFilesCron}")
	@Transactional(transactionManager = "appTransactionManager")
	public void makeDbBackupFiles() {
		// 데이터 전송 선 처리
		this.cron01();

		// 테이블 리스트를 분리
		String[] splitDbTableList = dbTableList.split("\\,");
		// 파일 경로 정의
		String dir = fileProperty.concat(backupFileDir).concat("backup(").concat(CoreDateUtils.getYyyyMMddHHmmssSSS().substring(0, 8).concat(")"));
		// 파일 sp, dim 선언
		String sp = "/";
		// 폴더를 만드는 파일 객체, sql 파일로 추출할 파일 객체 선언
		File makeFile = new File(dir), writerFile = null, mergeWriterFile = new File(dir.concat(sp).concat("DML").concat(ext));

		// 파일 존재여부 체크 및 생성
		if (!makeFile.exists()) {
			makeFile.mkdir();
		}

		try {

			if (!mergeWriterFile.exists()) {
				mergeWriterFile.createNewFile();
			}

			try(FileWriter mergeFw = new FileWriter(mergeWriterFile);
					PrintWriter mergeWriter = new PrintWriter(mergeFw);){

				// 테이블 리스트 만큼 모든 데이터를 조회해서 DELETE문과 INSERT문을 만들어서, 각 sql 파일을 생성하여 저장
				for(int i = 0; i < splitDbTableList.length; i++) {
					String dbTable = splitDbTableList[i];

					// 해당 테이블의 모든 데이터를 조회한다.
					List<LinkedHashMap<String, Object>> selectTableDataAllList = appDao.selectList("scheduledtasks.selectTableDataAllList", dbTable);

					// 파일 객체 생성
					writerFile = new File(dir.concat(sp).concat(dbTable).concat(ext));

					// 파일 존재여부 체크 및 생성
					if (!writerFile.exists()) {
						writerFile.createNewFile();
					}

					try(// File에 write할 수 있는 writer 생성
						FileWriter fw = new FileWriter(writerFile);
						PrintWriter writer = new PrintWriter(fw);){

						String deleteSql = "DELETE FROM ".concat(dbTable).concat(";");
						String nvlString = "";

						//테이블에 기존에 값을 지우고 새로 복원 할 것이므로 DELETE문 생성
						writer.println(deleteSql);
						//공백을 넣어서 구분 영역을 만들어준다.
						writer.println(nvlString);

						mergeWriter.println(deleteSql);
						mergeWriter.println(nvlString);

						for(int j = 0; j < selectTableDataAllList.size(); j++) {
							// 값 출력은 map 형태로 루프를 돌려서 필드 명 (key), 필드 값 (value)을 넣어서 INSERT 문을 만들어준다.
							LinkedHashMap<String, Object> selectTableDataMap = selectTableDataAllList.get(j);
							StringBuilder writerString = new StringBuilder("");

							writerString.append("INSERT INTO ")
							.append(dbTable)
							.append(" (");

							//key 값 세팅
							for(Entry<String, Object> entry : selectTableDataMap.entrySet()){
								writerString.append(entry.getKey().toString().toUpperCase())
											.append(",");
							}

							// 루프를 모두 돌린경우 마지막에 , 잔해가 남아있어서 마지막에 , 제거
							writerString.replace(writerString.lastIndexOf(","), writerString.length(), "");

							writerString.append(") VALUES (");

							//value 값 세팅
							for(Entry<String, Object> entry : selectTableDataMap.entrySet()){
								if(entry.getValue() != null) {
									//audit column이나 시간 형식의 컬럼인 경우 CURRENT_TIMESTAMP로 통일
									if("CRT_DTM".equals(entry.getKey()) || "MDF_DTM".equals(entry.getKey())) {
										writerString.append("CURRENT_TIMESTAMP")
													.append(",");
									//생성자 또는 수정자 컬럼에 BATCH로 값을 통일 하기 위해서 리스트를 만든다.
									} else if("CRTR".equals(entry.getKey()) || "AMDR".equals(entry.getKey()) || "UPDUSR".equals(entry.getKey())) {
										writerString.append("'")
													.append("BATCH")
													.append("'")
													.append(",");
									} else {
										writerString.append("'")
													.append(entry.getValue().toString().replace("'", "''"))
													.append("'")
													.append(",");
									}
								} else {
									writerString.append(entry.getValue())
												.append(",");
								}
							}

							// 루프를 모두 돌린경우 마지막에 , 잔해가 남아있어서 마지막에 , 제거
							writerString.replace(writerString.lastIndexOf(","), writerString.length(), "");

							// 끝 영역을 넣어서 INSERT문 작성을 마무리한다.
							writerString.append(");");

							// 파일에 쓰기
							mergeWriter.println(writerString.toString());
							writer.println(writerString.toString());
							writer.flush();
						}

						mergeWriter.println(nvlString);
					}
				}
				mergeWriter.flush();
			}
		} catch (Exception e) {
			log.error(Throwables.getStackTraceAsString(e));
		}
	}

	@Scheduled(cron = "${cmmn-properties.schedule-setting.cron01}")
	@Transactional(transactionManager = "moduleTransactionManager")
	public void cron01() {

		// 상품약관테이블 TB_CLUS_ITM_SET_MST 에서 DEL_YN 값이 Y 인 데이터들을 추출한다.
		List<PrdtClusDto> delTargetClusItmList = appDao.selectList("scheduledtasks.getDelTargetClusItmList");

		// 모듈쪽 상품약관테이블 TB_INDV_CLUS_PRDT_MST 에서
		// 위에서 추출한 상품약관테이블 TB_CLUS_ITM_SET_MST 에서 DEL_YN 값이 Y 인 데이터들을 삭제한다.
		deleteModulePrdtData(delTargetClusItmList);

		// 상품약관테이블 TB_CLUS_ITM_SET_MST 에서 DEL_YN 값이 Y 인 데이터들을 삭제한다.
		deleteSolPrdtData(delTargetClusItmList);

		// 상품약관테이블 TB_CLUS_ITM_SET_MST 에서 DATA_TRANS_STTCD (데이터전송상태코드) 값이 S 인 데이터들을 추출한다.
		// => DATA_TRANS_STTCD : S:전송대기, Y:전송성공, N:전송실패, F:시스템오류
		List<PrdtClusDto> transTargetClusItmList = appDao.selectList("scheduledtasks.getTransTargetClusItmList");

		// 모듈쪽 상품약관테이블 TB_INDV_CLUS_PRDT_MST 에서
		// 상품약관테이블 TB_CLUS_ITM_SET_MST 에서 DATA_TRANS_STTCD (데이터전송상태코드) 값이 S 인 데이터의 주계약에 해당되는 데이터들을 삭제한다.
		deleteModulePrdtData(transTargetClusItmList);

		// 모듈쪽 상품약관테이블 TB_INDV_CLUS_PRDT_MST 에서
		// 상품약관테이블 TB_CLUS_ITM_SET_MST 에서 DATA_TRANS_STTCD (데이터전송상태코드) 값이 S 인 데이터들을 추가한다.
		insertModulPrdData(transTargetClusItmList);

		// 상품약관테이블 TB_CLUS_ITM_SET_MST 에서 DATA_TRANS_STTCD (데이터전송상태코드) 값이 S 인 데이터들의 DATA_TRANS_STTCD 값을 Y 로 셋팅한다.
		setDataTransCdY(transTargetClusItmList);
	}

	@Transactional(transactionManager = "appTransactionManager")
	private void setDataTransCdY(List<PrdtClusDto> dataList) {
		dataList.forEach(item -> {
			appDao.update("scheduledtasks.setDataTransCdY", item);
		});
	}

	@Transactional(transactionManager = "moduleTransactionManager")
	private void insertModulPrdData(List<PrdtClusDto> dataList) {
		dataList.stream()
			// 주계약, 특약만 추출
			.filter(item -> "C".equals(item.getClusItmClcd()) || "T".equals(item.getClusItmClcd()) )
			.forEach(item -> moduleDao.insert("scheduledtasks.insertItmModule", item));
	}

	@Transactional(transactionManager = "appTransactionManager")
	private void deleteSolPrdtData(List<PrdtClusDto> dataList) {
		dataList.forEach(item -> {

			String clusItmCd = item.getClusItmCd();
			String prdtCd = item.getPrdtCd();

			// 삭제대상이 주계약인 경우...
			if(prdtCd.equals(clusItmCd)) {
				// 주계약에 딸린 모든 상품약관아이템들을 삭제한다.
				appDao.delete("scheduledtasks.deleteDelPrdtCdSol", item);
			}

			appDao.delete("scheduledtasks.deleteDelClusItmSol", item);
		});
	}

	@Transactional(transactionManager = "moduleTransactionManager")
	private void deleteModulePrdtData(List<PrdtClusDto> dataList) {
		dataList.forEach(item -> {
			String clusItmCd = item.getClusItmCd();
			String prdtCd = item.getPrdtCd();

			// 삭제대상이 주계약인 경우...
			if(prdtCd.equals(clusItmCd)) {
				// 주계약에 딸린 모든 상품약관아이템들을 삭제한다.
				moduleDao.delete("scheduledtasks.deleteDelPrdtCdModule", item);
			}

			moduleDao.delete("scheduledtasks.deleteDelClusItmModule", item);
		});
	}

}
