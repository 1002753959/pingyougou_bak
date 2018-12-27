app.service("uploadService",function($http){
	
	this.uploadFile = function(){
		// 向后台传递数据: 由于文件必须要用表单来传递,所以自己手动创建一个表单
		var formData = new FormData();
		// 向formData中添加数据: AngularJs的获取文件的方式 file是type为file标签的id
		formData.append("file",file.files[0]);
		
		return $http({
			method:'post',
			url:'../upload/uploadFile.do',
			data:formData,
            // Content-Type 不设置是: text/html  text/plain
			//在AngularJs中undefined默认是multipart/form-data 这种请求头用于传输文件
			headers:{'Content-Type':undefined} ,
			//AngularJs的异步提交
			transformRequest: angular.identity
		});
	}
	
});