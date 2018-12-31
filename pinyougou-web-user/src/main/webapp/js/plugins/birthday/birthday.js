(function ($) {
    //    jQuery.extend(object);为扩展jQuery类本身.为类添加新的方法。
    //主要实现了动态生成年月日,兵修改数据
    $.extend({
        ms_DatePicker: function (options) {
            //默认成员变量,可以是用外来的
            var defaults = {
                YearSelector: "#sel_year",
                MonthSelector: "#sel_month",
                DaySelector: "#sel_day",
                FirstText: "--",
                FirstValue: 0,
                Year_Str: "--",
                Month_Str: "--",
                Day_Str: "--"
            };
            //取出外来的数据,如果没有设置就用默认的
            var opts = $.extend({}, defaults, options);
            //根据id获取jquery对象
            var $YearSelector = $(opts.YearSelector);
            var $MonthSelector = $(opts.MonthSelector);
            var $DaySelector = $(opts.DaySelector);
            var FirstText = opts.FirstText;
            var FirstValue = opts.FirstValue;
            var Year_Str = opts.Year_Str;
            var Month_Str = opts.Month_Str;
            var Day_Str = opts.Day_Str;
            // 初始化生成html标准样式标记为--样式
            var str = "<option value=\"" + FirstValue + "\">" + FirstText + "</option>";
            $YearSelector.val(Year_Str);
            $MonthSelector.val(Month_Str);
            $DaySelector.val(Day_Str);

            function SetYear(Year_Str) {
                // 年份列表
                var yearNow = new Date().getFullYear();
                var yearStr;
                for (var i = yearNow; i >= 1900; i--) {
                    //判断获取的值如果为rel中默认值就标记为默认,但是原始配合anglua不好用需要改造
                    var sed = Year_Str == i ? "selected" : "";
                   yearStr += "<option value=\"" + i + "\" " + sed + ">" + i + "</option>";


                }
                //追加生成的1900-现在日期的html代码
                $YearSelector.html(yearStr);
            }

            function SetMonth(Month_Str) {
                // 月份列表
                var monthStr;
                    for (var i = 1; i <= 12; i++) {
                        var sed = Month_Str == i ? "selected" : "";
                        monthStr+= "<option value=\"" + i + "\" " + sed + ">" + i + "</option>";
                    }
                $MonthSelector.html(monthStr);
            }
            // 日列表(仅当选择了年月)
            function BuildDay(Day_Str) {
                if ($YearSelector.val() == 0 || $MonthSelector.val() == 0) {
                    // 未选择年份或者月份,如果任意一个没有元素则设置月为"--"显示的样式("--"这种也是可以初始化自定义例如++,==,**等等)
                    $DaySelector.html(Day_Str);
                } else {
                    //先设置为--
                    // $DaySelector.html(Day_Str);
                    //获取更改后的值
                    var year = parseInt($YearSelector.val());
                    var month = parseInt($MonthSelector.val());
                    var dayCount = 0;
                    //判断闰月天数
                    switch (month) {
                        case 1:
                        case 3:
                        case 5:
                        case 7:
                        case 8:
                        case 10:
                        case 12:
                            dayCount = 31;
                            break;
                        case 4:
                        case 6:
                        case 9:
                        case 11:
                            dayCount = 30;
                            break;
                        case 2:
                            dayCount = 28;
                            if ((year % 4 == 0) && (year % 100 != 0) || (year % 400 == 0)) {
                                dayCount = 29;
                            }
                            break;
                        default:
                            break;
                    }
                    //计算出月份,生成标签
                    var dayStr;
                    for (var i = 1; i <= dayCount; i++) {
                        var sed= Day_Str == i ? "selected" : "";
                        dayStr += "<option value=\"" + i + "\" " + sed + ">" + i + "</option>";

                    }
                    $DaySelector.html(dayStr);

                }


            }
            // 日列表(仅当选择了年月)
            function BuildDayByMonth(month) {
                if ($YearSelector.val() == 0 || $MonthSelector.val() == 0) {
                    // 未选择年份或者月份,如果任意一个没有元素则设置月为"--"显示的样式("--"这种也是可以初始化自定义例如++,==,**等等)
                    $DaySelector.html(Day_Str);
                } else {
                    //先设置为--
                    // $DaySelector.html(Day_Str);
                    //获取更改后的值
                    var year = parseInt($YearSelector.val());
                    // var month = parseInt($MonthSelector.val());
                    var dayCount = 0;
                    //判断闰月天数
                    switch (month) {
                        case 1:
                        case 3:
                        case 5:
                        case 7:
                        case 8:
                        case 10:
                        case 12:
                            dayCount = 31;
                            break;
                        case 4:
                        case 6:
                        case 9:
                        case 11:
                            dayCount = 30;
                            break;
                        case 2:
                            dayCount = 28;
                            if ((year % 4 == 0) && (year % 100 != 0) || (year % 400 == 0)) {
                                dayCount = 29;
                            }
                            break;
                        default:
                            break;
                    }
                    //计算出月份,生成标签
                    var dayStr;
                    for (var i = 1; i <= dayCount; i++) {
                        var sed= Day_Str == i ? "selected" : "";
                        dayStr += "<option value=\"" + i + "\" " + sed + ">" + i + "</option>";

                    }
                    $DaySelector.html(dayStr);

                }


            }
            // 年更新清空月
            function BuildDayByYear() {
                    $DaySelector.html("");
            }
            SetYear(Year_Str);
            SetMonth(Month_Str);
            BuildDay(Day_Str);

            $MonthSelector.change(function () {
                //实时更新数据月份,修改Andular里的字段
                var month = parseInt($MonthSelector.val());
                var input = angular.element($MonthSelector);
                var scope = input.scope();
                scope.month_number=month;
                scope.$apply();
                BuildDayByMonth(month);

            });
            $YearSelector.change(function () {
                //实时更新数据年,修改Andular里的字段
                var year = parseInt($YearSelector.val());
                var input = angular.element($YearSelector);
                var scope = input.scope();
                scope.year_number=year;
                scope.$apply();
                BuildDayByYear();
                SetMonth(1);
                BuildDay(1);


                var input1 = angular.element($MonthSelector);
                var scope1 = input1.scope();
                scope1.month_number=1;
                scope1.$apply();

                var input2 = angular.element($DaySelector);
                var scope2 = input2.scope();
                scope2.day_number=1;
                scope2.$apply();
            });
            $DaySelector.change(function () {
                //实时更新数据日,修改Andular里的字段
                var day = parseInt($DaySelector.val());
                var input = angular.element($DaySelector);
                var scope = input.scope();
                scope.day_number=day;
                scope.$apply();


            });
        }
    });
})(jQuery);