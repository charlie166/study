package cn.charlie166.study.web.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
* @ClassName: FileUtils 
* @Description: 文件操作工具类
* @company 
* @author liyang
* @Email charlie166@163.com
* @date 2017年6月9日 
*
 */
public class FileUtils {

	private static Logger logger = LoggerFactory.getLogger(FileUtils.class);
	
	/**
	* @Title: getFileWithSuffix 
	* @Description: 获取文件夹下所有指定格式文件
	* @param direction 查询文件夹
	* @param suffix 匹配文件格式。未指定时，返回所有文件
	* @return 符合要求的文件列表
	 */
	public static List<File> getFileWithSuffix(String direction, String suffix){
		return FileUtils.getPathWithSuffix(direction, suffix).stream().map(one -> one.toFile()).collect(Collectors.toList());
	}
	
	/**
	* @Title: getPathWithSuffix 
	* @Description: 获取文件夹下所有指定格式文件
	* @param direction 查询文件夹
	* @param suffix 匹配文件格式。未指定时，返回所有文件
	* @return 符合要求的文件列表
	 */
	public static List<Path> getPathWithSuffix(String direction, String suffix){
		logger.debug("遍历文件夹[" + direction + "]格式[" + suffix + "]");
		if(StringUtils.hasContent(direction)){
			Path path = Paths.get(direction);
			try {
				List<Path> list = new ArrayList<Path>();
				Files.walkFileTree(path, new FileVisitor<Path>() {
					@Override
					public FileVisitResult preVisitDirectory(Path dir,
							BasicFileAttributes attrs) throws IOException {
						return FileVisitResult.CONTINUE;
					}
					@Override
					public FileVisitResult visitFile(Path file,
							BasicFileAttributes attrs) throws IOException {
						if(StringUtils.isNullOrEmpty(suffix) || file.toString().toLowerCase().endsWith(suffix.toLowerCase())){
							list.add(file);
						}
						return FileVisitResult.CONTINUE;
					}
					@Override
					public FileVisitResult visitFileFailed(Path file,
							IOException exc) throws IOException {
						return FileVisitResult.CONTINUE;
					}
					@Override
					public FileVisitResult postVisitDirectory(Path dir,
							IOException exc) throws IOException {
						return FileVisitResult.CONTINUE;
					}
				});
				return list;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return Collections.emptyList();
	}
	
	public static void listenDirectionCreate(Path direction){
		logger.debug("监听文件夹[" + direction.toString() + "]");
		if(direction != null && Files.isDirectory(direction)){
			try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
	            //给path路径加上文件观察服务
				direction.register(watchService, 
					StandardWatchEventKinds.ENTRY_CREATE,
					StandardWatchEventKinds.ENTRY_MODIFY,
					StandardWatchEventKinds.ENTRY_DELETE);
	            while (true) {
	                final WatchKey key = watchService.take();
	                for (WatchEvent<?> watchEvent : key.pollEvents()) {
	                    final WatchEvent.Kind<?> kind = watchEvent.kind();
	                    if (kind == StandardWatchEventKinds.OVERFLOW) {
	                        continue;
	                    }
	                    @SuppressWarnings("unchecked")
	                    final WatchEvent<Path> watchEventPath = (WatchEvent<Path>) watchEvent;
	                    final Path filename = watchEventPath.context();
	                    logger.debug(kind + " -> " + filename);
	                    if (kind == StandardWatchEventKinds.ENTRY_CREATE) {

	                    }
	                    if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {

	                    }
	                    if (kind == StandardWatchEventKinds.ENTRY_DELETE) {

	                    }
	                }
	                boolean valid = key.reset();
	                /***exit loop if the key is not valid (if the directory was deleted,for)***/
	                if (!valid) {
	                    break;
	                }
	            }

	        } catch (IOException | InterruptedException ex) {
	        	 logger.error("监听异常", ex);
	        }
		}
	}
}	