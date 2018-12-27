app.controller("baseController",function ($scope) {

    //分页控件
    $scope.paginationConf = {
        currentPage: 1, //当前页
        totalItems: 0,//总商品数
        itemsPerPage: 5, //每页显示的条数
        perPageOptions: [5, 10, 20, 30, 40], //每页显示条数的选项
        onChange: function () {
            //上面这四个参数有任何一个变了就会执行这个重新加载的方法
            $scope.reloadList();
        }
    };
    //重新加载的方法
    /*
    * 页面有的数据:当前页  每页显示的条数  一共有多少页  这三项页面都有就不用后台封装了
    * 页面没有的数据:商品总条数  每页的数据集合  这两个数据需要后天查,最后的pageBean只需要封装这两个属性
    * */
    $scope.reloadList =function () {
        //调用携带数据的分页方法
        $scope.search($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
    }

    //定义一个存放id的数组
    $scope.selectIds = [];
    //点击复选框的时候跟新id数组中的值
    //$event参数能获取到当前那个标签
    $scope.updateSelection = function ($event, id) {
        //判断当前这个 复选框是否选中
        if ($event.target.checked) {
            //选中,把id添加到数组中
            $scope.selectIds.push(id);
        } else {
            //这是选中后又取消了,把这个id从数组中删除
            //找出id在这个数组中的索引.注意index是个数组
            var index = $scope.selectIds.indexOf(id);
            //再根据索引删除,第一个参数索引值,第二个参数删除几个
            $scope.selectIds.splice(index, 1);
        }

    }


    // 定义方法：获取JSON字符串中的某个key对应值的集合
    $scope.jsonToString = function(jsonStr,key){
        // 将字符串转成JSOn:
        var jsonObj = JSON.parse(jsonStr);

        var value = "";
        for(var i=0;i<jsonObj.length;i++){

            if(i>0){
                value += ",";
            }

            value += jsonObj[i][key];
        }
        return value;
    }
});