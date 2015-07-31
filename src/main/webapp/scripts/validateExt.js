$.extend($.fn.validatebox.defaults.rules, {
    /* 实例 */
    minLength: {
        validator: function(value, param){
            return value.length >= param[0];
        },
        message: '至少{0}字符.'
    },
    
    /* 实例 */
    maxLength: {
        validator: function(value, param){
            return value.length <= param[0];
        },
        message: '不能超过{0}字符.'
    },
    
    /* 数字校验：number[min,max] */
    number: {
        validator: function(value, param){
            if(checkNUM(value) == 0) {
            	return false;
            }
            if(value < param[0] || value > param[1]) {
            	return false;
            }
            return true;
        },
        message: '请输入{0}~{1}的整数'
    },
    
    /* 数字校验2：isNumber */
    isNumber: {
        validator: function(value){
            if(checkNUM(value) == 0) {
            	return false;
            }
            return true;
        },
        message: '请输入整数'
    },
    
    /* 金额校验：amount[min,max] */
    amount: {
    	validator: function(value, param) {
        	 if (isAmount(value) == 0) {  
        	 		return false;
        	 }
            if(value < param[0] || value > param[1]) {
            	return false;
            }
            return true;
    	},
    	message: '请输入{0}~{1}的金额，不超过两位小数'
    },
    
    /* 金额校验：isAmount */
    isAmount: {
        validator: function(value){      
        	 if (isAmount(value) == 0) {  
        	 		return false;
        	 }
        	 return true;
        },
        message: '请输入金额，不超过两位小数'
    },
    
    /* 字符校验：string[min,max]*/
    string: {
        validator: function(value, param){
			var str = ATrim(value);
            if(str.length < param[0] || str.length > param[1]) {
            	return false;
            }
            return true;
        },
        message: '请输入{0}~{1}个字符'
    },
    
    /* 手机校验：mobile */
    mobile: {
        validator: function(value){
            return isMobile(value);
        },
        message: '手机格式不正确'
    },
    
    /* 金额校验：number[min,max] */
    money: {
        validator: function(value){
        	 var patrn = /^-?\d+\.{0,}\d{0,}$/;       
        	 if (!patrn.exec(value)) {  
        	 		return false;
        	 }
            return true;
        },
        message: '请正确输入金额'
    },
    /* 包含两位小数点的小数校验：floatNumber */
    floatNumber: {
        validator: function(value){
			// var regu = /(^([1-9]\d*|[0])\.\d{1,2}$|^[1-9]\d*$)/;
			var regu=/^\d+(\.\d{1,2})?$/ ;
			var re = new RegExp(regu);
			return re.test(value);
        },
        message: '请输入正确的折扣'
    }
});