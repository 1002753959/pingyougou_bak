 //控制层 
app.controller('orderInfoController' ,function($scope,$controller,$location,$window,orderInfoService){

    //$controller('baseController',{$scope:$scope});//继承

    $scope.time=0;
    //定义下拉框时间数组
    //$scope.dataList=[["在线支付","货到付款"],["在线支付","货到付款"],["在线支付","货到付款"]];
    //$scope.data = ["在线支付","货到付款"];

    var dataList = new Array(["第一个值","第一个值2"],["第二个值","第二个值2"]);
    $scope.dataIndex = 0;
    var item = dataList [$scope.dataIndex];
    $scope.xlist = item;
    //读取列表数据绑定到表单中
    $scope.findLine=function(){
        var item = dataList [$scope.dataIndex];
        alert(item);
            orderInfoService.findLine($scope.time).success(
                function(response){
                    $scope.list=response;
                    //$scope.paginationConf.totalItems=response.total;//更新总记录数
                }
            )

    }

    //查询折线图的方法
    $scope.findLine=function(){
        orderInfoService.findAll().success(
            orderInfoService.findAll($scope.time).success(
                function(response){
                    // 基于准备好的dom，初始化echarts实例
                    var myChart = echarts.init(document.getElementById('main'));

                    // 指定图表的配置项和数据
                    var option = {
                        // 标题
                        title: {
                            text: '销售折线图'
                        },
                        // 工具箱
                        toolbox: {
                            show: true,
                            feature: {
                                dataView:{
                                    show:true
                                },
                                restore:{
                                    show:true
                                },
                                dataZoom:{
                                    show:true
                                },
                                saveAsImage: {
                                    show: true
                                },
                                magicType:{
                                    type:['line','bar']
                                }
                            }
                        },
                        tooltip:{
                            trigger:'axis'
                        },
                        // 图例
                        legend: {
                            data: ['销量']
                        },
                        // x轴
                        xAxis: {
                            data: ["前五周", "前四周", "前三周", "前二周",  "前一周","本周"]
                            // data: xlist
                        },
                        yAxis: {},
                        // 数据
                        series: [
                            //     {
                            //     name: '销量',
                            //     type: 'bar',
                            //     data: [5, 20, 36, 10, 10, 20]
                            // },
                            {
                                name: '销售量',
                                type: 'line',
                                data: response ,
                                markPoint:{
                                    data:[
                                        {type:'max',name:'最大值'},
                                        {type:'min',name:'最小值'}
                                    ]
                                },
                                markLine:{
                                    data:[
                                        {type:'average',name:'平均值'}
                                    ]
                                }
                            }]
                    };

                    // 使用刚指定的配置项和数据显示图表。
                    myChart.setOption(option);
                }
            )
        )
    }

    // $scope.searchEntity={};//定义搜索对象




    //点击复选框更新ids数组
    $scope.selectIds = [];
    $scope.updateSelection = function ($event,id) {
        //判断这个复选框是否被选中
        if ($event.target.checked) {
            //选中了添加到数组总
            $scope.selectIds.push(id);
        }else {
            //没有选中,找到索引删掉
            var index = $scope.selectIds.indexOf(id);
            $scope.selectIds.splice(index,1);

        }

    };

});
