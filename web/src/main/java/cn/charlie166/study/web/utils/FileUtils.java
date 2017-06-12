package cn.charlie166.study.web.utils;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

	/**
	* @Title: getFileWithSuffix 
	* @Description: 获取文件夹下所有指定格式文件
	* @param direction 查询文件夹
	* @param suffix 匹配文件格式。未指定时，返回所有文件
	* @return 符合要求的文件列表
	 */
	public static List<Path> getFileWithSuffix(String direction, String suffix){
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
						if(StringUtils.isNullOrEmpty(suffix) || file.toString().endsWith(suffix)){
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
}	