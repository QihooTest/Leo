package com.github.qihootest.leo.toolkit.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;


/**
 * 编译java文件为class
 * @author @<a href='http://weibo.com/bwgang'>bwgang</a> (bwgang@163.com)<br/>
 *
 */
public class CompilerUtil {   
	
	//日志记录
	private static LogUtil log=LogUtil.getLogger(CompilerUtil.class);
	
	/**
	 * 编译java文件为class
	 * @param javaFilePath  java文件或包含java文件的目录
	 * @param distPath      项目class文件存放目录
	 * @param libPath       jar包存放目录
	 * @param jarFile       jar包存放目录(maven)
	 * @return boolean 编译成功返回true
	 */
    public static boolean dynamicCompiler(String javaFilePath,String distPath,String libPath,String jarFile) {
    	DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();  
        boolean compilerResult = compiler("UTF-8",getClassPath(libPath,distPath,jarFile),javaFilePath, distPath, diagnostics);   
        if (compilerResult) {
        	log.info("动态编译成功："+javaFilePath+"  输出目录为："+distPath);
        }else{
        	log.error("动态编译失败："+javaFilePath);
        	for (@SuppressWarnings("rawtypes") Diagnostic diagnostic : diagnostics.getDiagnostics()) {   
        		log.error(diagnostic.getMessage(null));  
            } 
        }
        return compilerResult;
    }
    
    /**
     * 编译java文件  
     * @param encoding 编译编码  
     * @param jars 需要加载的jar目录或文件  
     * @param filePath 文件或者目录（若为目录，编译目录下所有java文件）  
     * @param sourceDir 项目源码目录  
     * @param distDir 编译后class类文件存放目录  
     * @param diagnostics 存放编译过程中的错误信息  
     * @return   boolean
     */  
    private static  boolean compiler(String encoding,String jars,String filePath, String distDir, DiagnosticCollector<JavaFileObject> diagnostics){    
        // 获取编译器实例   
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();   
        // 获取标准文件管理器实例   
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);   
        //编译文件
        if (StringUtil.IsNullOrEmpty(filePath)) {   
        	log.info("待编译的文件或目录不存在");
            return false;   
        } 
        //输出目录
        if (!FileUtil.createDictory(distDir)) {   
        	log.error("输出目录创建失败");
            return false;   
        } 
        // 得到filePath目录下的所有java源文件   
        File sourceFile = new File(filePath);   
        List<File> sourceFileList = new ArrayList<File>();   
        sourceFileList = getSourceFiles(sourceFile);   
        // 没有java文件，直接返回   
        if (sourceFileList.size() == 0) {   
        	log.error(filePath + "目录下查找不到任何java文件");
            return false;   
        }   
        // 获取要编译的编译单元   
        Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(sourceFileList);   
        //编译选项，在编译java文件时，编译程序会自动的去寻找java文件引用的其他的java源文件或者class。 -classpath选项就是定义class文件的查找目录。  
        Iterable<String> options = Arrays.asList("-encoding",encoding,"-classpath",jars,"-d", distDir);   
        CompilationTask compilationTask = compiler.getTask(null, fileManager, diagnostics, options, null, compilationUnits);   

        // 运行编译任务   
        boolean res= compilationTask.call();
        try {
			fileManager.close();
		} catch (IOException e) {
			log.error("关闭文件管理器失败");
			log.error(e.getMessage());
		}
        return res;
    }   

    /**
     * 查找该目录下的所有的java文件  
     * @param sourceFile  
     * @return List<File>
     */  
    private static  List<File> getSourceFiles(File sourceFile){   
    	List<File> sourceFileList = new ArrayList<File>();
        if (sourceFile.exists() && sourceFileList != null) {//文件或者目录必须存在   
            if (sourceFile.isDirectory()) {// 若file对象为目录
            	 // 得到该目录下以.java结尾的所有文件   
            	List<String> sourceFileListTmp=FileUtil.getFilesFromFolder(sourceFile.getPath(),"java");
            	for (String filePath : sourceFileListTmp) {
            		sourceFileList.add(new File(filePath));
				}
            } else {// 若file对象为文件   
                sourceFileList.add(sourceFile);   
            }   
        }else{
        	log.error("目录或文件不存在: "+sourceFile);
        }
        return sourceFileList;
    }   

    /**
     * 查找该目录下的所有的jar文件
     * @param libPath
     * @return String
     */
    private static  String  getJarFiles(String libPath,String jarFile) {   
    	// 得到该目录下以.jar结尾的所有文件
    	String jarsPath="";
    	List<String> jarFilePath = FileUtil.getFilesFromFolder(libPath,"jar");
    	for (String filePath : jarFilePath) {
    		jarsPath=jarsPath+filePath+";"; 
		}
    	jarsPath=jarsPath+jarFile;
        return jarsPath;   
    }   
    
    private static String getClassPath(String libPath,String classPath,String jarFile){
    	if (FileUtil.isExist(classPath)) {
			return getJarFiles(libPath,jarFile)+classPath;
		}else{
			log.error("classPath不存在："+classPath);
			return getJarFiles(libPath,jarFile);
		}
    }
}  

 
 
 

