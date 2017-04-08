package org.apache.log4j;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.helpers.CountingQuietWriter;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;

public class MyLog4jRollingFileAppender extends RollingFileAppender {
	private long nextRollover = 0;
	private static Map<String, BeginFileData> fileMaps = new HashMap<String, BeginFileData>();
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

	
	public void rollOver() {
		File target;
		File file;
		int maxBackupIndexLeng = String.valueOf(maxBackupIndex).length();
		if (qw != null) {
			long size = ((CountingQuietWriter) qw).getCount();
			LogLog.debug("rolling over count=" + size);
			nextRollover = size + maxFileSize;
		}
		LogLog.debug("maxBackupIndex=" + maxBackupIndex);
		String nowDateString = sdf.format(new Date());
		String newFileName = (fileName.indexOf(".") != -1 ? fileName.substring(
				0, fileName.lastIndexOf(".")) : fileName);
		boolean renameSucceeded = true;
		
		if (maxBackupIndex > 0) {
			
			file = new File(newFileName + '_'
					+ getIndex(maxBackupIndex, maxBackupIndexLeng));
			if (file.exists()) {
				renameSucceeded = file.delete();
			}
			for (int i = maxBackupIndex - 1; (i >= 1 && renameSucceeded); i--) {
				file = new File(newFileName + '_'
						+ getIndex(i, maxBackupIndexLeng));
				if (file.exists()) {
					target = new File(newFileName + '_'
							+ getIndex(i + 1, maxBackupIndexLeng));
					LogLog.debug("Renaming file " + file + " to " + target);
					renameSucceeded = file.renameTo(target);
				}
			}
			if (renameSucceeded) {
				BeginFileData beginFileData = fileMaps.get(fileName);
				
				if (newFileName.indexOf(nowDateString) == -1
						&& beginFileData.getFileName().indexOf("yyyy/MM/dd") != -1) {
					newFileName = beginFileData.getFileName().replace(
							"yyyy/MM/dd", nowDateString);
					newFileName = (newFileName.indexOf(".") != -1 ? newFileName
							.substring(0, newFileName.lastIndexOf("."))
							: newFileName);
				}
				target = new File(newFileName + '_'
						+ getIndex(1, maxBackupIndexLeng));
				this.closeFile();
				file = new File(fileName);
				LogLog.debug("Renaming file " + file + " to " + target);
				renameSucceeded = file.renameTo(target);
				
				if (!renameSucceeded) {
					try {
						this.setFile(fileName, true, bufferedIO, bufferSize);
					} catch (IOException e) {
						LogLog.error("setFile(" + fileName
								+ ", true) call failed.", e);
					}
				}
			}
		}
		
		if (renameSucceeded) {
			try {
				this.setFile(fileName, false, bufferedIO, bufferSize);
				nextRollover = 0;
			} catch (IOException e) {
				LogLog
						.error("setFile(" + fileName + ", false) call failed.",
								e);
			}
		}
	}

	
	private String getIndex(int i, int maxBackupIndexLeng) {
		String index = String.valueOf(i);
		int len = index.length();
		for (int j = len; j < maxBackupIndexLeng; j++) {
			index = "0" + index;
		}
		return index + ".log";
	}

	
	protected void subAppend(LoggingEvent event) {
		super.subAppend(event);
		if (fileName != null && qw != null) {
			String nowDate = sdf.format(new Date());
			
			if (!fileMaps.get(fileName).getDate().equals(nowDate)) {
				rollOver();
				return;
			}
			
			long size = ((CountingQuietWriter) qw).getCount();
			
			
			if (size >= maxFileSize && size >= nextRollover) {
				rollOver();
			}
		}
	}

	@Override
	public synchronized void setFile(String fileName, boolean append,
			boolean bufferedIO, int bufferSize) throws IOException {
		String nowDate = sdf.format(new Date());
		
		if (fileName.indexOf("yyyy/MM/dd") != -1) {
			String beginFileName = fileName;
			fileName = fileName.replace("yyyy/MM/dd", nowDate);
			fileMaps.put(fileName, new BeginFileData(beginFileName, nowDate));
		}
		BeginFileData beginFileData = fileMaps.get(fileName);
		
			
		if (!beginFileData.getDate().equals(nowDate)) {
			
			beginFileData.setDate(nowDate);
			fileName = beginFileData.getFileName().replace("yyyy/MM/dd", nowDate);
			fileMaps.put(fileName, beginFileData);
		}
		super.setFile(fileName, append, this.bufferedIO, this.bufferSize);
	}


	class BeginFileData {
		public BeginFileData(String fileName, String date) {
			super();
			this.fileName = fileName;
			this.date = date;
		}

		private String fileName;
		private String date;

		public String getFileName() {
			return fileName;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
		}

		public String getDate() {
			return date;
		}

		public void setDate(String date) {
			this.date = date;
		}
	}
}