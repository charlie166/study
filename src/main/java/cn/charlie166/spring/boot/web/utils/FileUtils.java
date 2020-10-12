package cn.charlie166.spring.boot.web.utils;

import cn.charlie166.spring.boot.web.exception.CustomException;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 文件工具类
 */
public class FileUtils {

    private FileUtils(){}

    /**
     * 判断文件是否可读
     * @param filePath 文件路径
     * @return 执行文件的对象
     * @throws CustomException 失败原因
     */
    public static Path checkFileReadable(String filePath) throws CustomException {
        if (StringUtils.isBlank(filePath)) {
            throw CustomException.t("文件路径不能为空");
        }
        Path file = Paths.get(filePath);
        if (Files.notExists(file) || !Files.isReadable(file)) {
            throw CustomException.t("文件不存在或不可读");
        }
        if (!Files.isRegularFile(file)) {
            throw CustomException.t("不是有效文件");
        }
        return file;
    }
}
