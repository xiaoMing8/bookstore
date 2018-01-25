package com.langsin.book.web.servlet.admin;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.ImageIcon;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import cn.itcast.commons.CommonUtils;

import com.langsin.book.domain.Book;
import com.langsin.book.service.BookService;
import com.langsin.category.domain.Category;
import com.langsin.category.service.CategoryService;
public class AdminUploadBookServlet extends HttpServlet {

	private BookService bookService = new BookService();
	private CategoryService categoryService = new CategoryService();
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		doPost(request, response);
	}

	//添加图书(包括上传图片)
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		/**
		 * 一:封装表单数据 book
		 * 		1.上传三步: 
		 * 				创建工厂类 ;  创建解析器对象(对上传文件的大小进行限制);   解析request对象(inputStream)(进行文件格式的检测)
		 *      2. 判断是修改图书还是添加图书
		 * 二.将表单数据封装到book中(普通表单字段)
		 * 三.保存上传的文件,并设置book的image属性
		 * 四.保存成功后 检验图片的尺寸   不合适保存信息转发
		 * 五.将book添加到数据库中
		 */
		//创建工厂
		DiskFileItemFactory factory = new DiskFileItemFactory();
		//创建解析器对象
		ServletFileUpload sfu = new ServletFileUpload(factory);
		//对上传单个文件的大小进行设置
		sfu.setFileSizeMax(30 * 1024);
		
		//解析request对象
		try {
			List<FileItem> fileItemList = sfu.parseRequest(request); // 一个fileItem对应一个表单字段
			
			for (FileItem fileItem : fileItemList) {
				if ("modifyImage".equals(fileItem.getFieldName())) {
					String image = fileItem.getString("UTF-8");
						//说明这是修改图书的请求				
							modifyBook(request, response, fileItemList,image);
					return ;
				}
			}
			
			checkInfoByAdd(fileItemList,request,response);


			//得到客户端上传的原始文件名称
			String uploadFileName = fileItemList.get(1).getName();
			//检验上传文件的格式(扩展名)
			checkFileFormat(request,response,uploadFileName);	
			//将表单中的普通表单字段封装到book中(包括分类信息)
			Book book = getBookByCommons(fileItemList);
			//保存上传的文件,并返回该文件
			File destFile = saveUploadFile(request,response,fileItemList);
			checkImageSize(destFile,200,200,request,response);

			/**
			 * 校验无误后:
			 * 	  为book设置image属性并保存在数据库中
			 */
			// 保存之后开始设置book的image属性
			// 即把图片在磁盘中保存的名称设置给image属性
			book.setImage("book_img/" + destFile.getName());
			// 五.将book添加到数据库,转发
			bookService.addBook(book);
			request.getRequestDispatcher("/admin/AdminBookServlet?method=findAll")
					.forward(request, response);
		} catch (Exception e) {
			// 判断单个文件大小是否超过指定尺寸
			if (e instanceof FileUploadBase.FileSizeLimitExceededException) {
				request.setAttribute("msg", "您上传的文件超出了30KB");
				request.setAttribute("categoryList", categoryService.findAll());
				request.getRequestDispatcher("/adminjsps/admin/book/add.jsp")
						.forward(request, response);
			}

		}

	}



	private void checkInfoByAdd(List<FileItem> fileItemList,
			HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		for (FileItem fileItem : fileItemList) {
			System.out.println(fileItem);
			if (fileItem.getSize() == 0) {
				// 保存错误信息
				request.setAttribute("msg", "您上传的信息不完整,请重新上传!");
				// 保存分类信息
				request.setAttribute("categoryList", categoryService.findAll());
				// 转发 add.jsp
				request.getRequestDispatcher("/adminjsps/admin/book/add.jsp")
						.forward(request, response);
				return;
			}
		}
	}

	private void checkImageSize(File destFile, int x, int y,
			HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Image image = new ImageIcon(destFile.getAbsolutePath()).getImage();
		if (image.getWidth(null) > x || image.getHeight(null) > y) {
			destFile.delete();// 删除这个文件！
			request.setAttribute("msg", "您上传的图片尺寸超出了 "+x+" * "+y+"");
			request.setAttribute("categoryList", categoryService.findAll());
			request.getRequestDispatcher("/adminjsps/admin/book/add.jsp")
					.forward(request, response);
			return;
		}

	}

	//保存上传的文件
	private File saveUploadFile(HttpServletRequest request,
			HttpServletResponse response, List<FileItem> fileItemList) throws Exception {
		/**
		 * 二.保存上传的文件 
		 * 1.得到在磁盘上的保存目录 
		 * 2.得到保存的文件名称
		 */
		// 得到文件保存在磁盘上的真实路径(带盘符的那种)
		String savePath = this.getServletContext().getRealPath("/book_img");

		// 得到上传的文件名称,增加uuid(为防止上传的文件名称冲突)设置为本地保存的名称
		String saveFileName = CommonUtils.uuid() + "_" + fileItemList.get(1).getName();

		// 使用目录(文件保存地址)以及文件名创建目标文件
		File destFile = new File(savePath, saveFileName);
		// 将上传的文件写到目标文件中
		fileItemList.get(1).write(destFile);
		return destFile;
		
	}


	//封装普通表单字段到book中
	private Book getBookByCommons(List<FileItem> fileItemList) throws UnsupportedEncodingException {
		/**
		 * 把表单中的信息封装到book里:
		 *    1.先将普通的表单字段封装到map中 
		 *    2.将map中的数据封装到book,category中并建立关系
		 */
		Map<String, String> map = new HashMap<String, String>();
		for (FileItem fileItem : fileItemList) {
			if (fileItem.isFormField()) {// true为普通表单字段
				// 我们用表单名称做键,表单的值做值
				map.put(fileItem.getFieldName(), fileItem.getString("UTF-8"));
			}
		}
		
		for (String key : map.keySet()) {
			System.out.println("key= " + key + " and value= " + map.get(key));
		}
		
		Book book = CommonUtils.toBean(map, Book.class);
		
		System.out.println(book);
		if(book.getBid()==null){//说明是添加图书			
			// 补全bid
			book.setBid(CommonUtils.uuid());
		}
		book.setDel(false);
		Category category = CommonUtils.toBean(map, Category.class);
		// 建立关系
		book.setCategory(category);
		return book;
	}

	//检查上传文件的格式                 
	private void checkFileFormat(HttpServletRequest request,
			HttpServletResponse response, String uploadFileName) throws ServletException, IOException {
		if (!uploadFileName.toLowerCase().endsWith(".jpg")) {
			// 保存错误信息
			request.setAttribute("msg", "您上传的图片不是JPG格式的,请重新上传!");
			// 保存分类信息
			request.setAttribute("categoryList", categoryService.findAll());
			// 转发 add.jsp
			request.getRequestDispatcher("/adminjsps/admin/book/add.jsp")
					.forward(request, response);
			return ;
		}

	}


	//修改图书
	private void modifyBook(HttpServletRequest request,
			HttpServletResponse response, List<FileItem> fileItemList,
			String image) throws Exception {

		// 封装普通表单项
		Book book = getBookByCommons(fileItemList);
		// 如果没有上传文件
		if (fileItemList.get(1).getSize() == 0) {
			// 设置原有的image
			book.setImage(image);
		} else {
			// 得到客户端上传的原始文件名称
			String uploadFileName = fileItemList.get(1).getName();
			// 保存上传文件
			File destFile = saveUploadFile(request, response, fileItemList);
			// 检验上传文件的格式(扩展名)
			checkFileFormat(request, response, uploadFileName);
			/**
			 * 校验无误后: 为book设置image属性并保存在数据库中
			 */
			// 保存之后开始设置book的image属性
			// 即把图片在磁盘中保存的名称设置给image属性
			book.setImage("book_img/" + destFile.getName());
			
		}

		// 五.将book添加到数据库,转发
		bookService.modifyBook(book);
		request.getRequestDispatcher("/admin/AdminBookServlet?method=findAll")
		.forward(request, response);
	}
}
