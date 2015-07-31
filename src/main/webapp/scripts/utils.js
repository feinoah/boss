$.filteNull = function (obj)
{
	if(obj == null)
	{
		return '';
	}
	
	return obj;
}

$.lpad = function(str,length,pad)
{
	var newString ="";
	str = new String(str);
	
	for(var i=0;i<length-str.length;i++)
	{
		newString=pad +newString;
	}
	
	newString = newString+str;
	
	return newString;
}


$.fmtDateTime = function(datetime)
{
	//"merchant_settle_date":{"date":28,"day":4,"hours":16,"minutes":17,"month":2,"nanos":0,"seconds":28,"time":1364458648000,"timezoneOffset":-480,"year":113}
	//2013-03-28 16:17:28
	if(datetime==null){
		return "";
	}
	var date = datetime.date;
	var day = datetime.day;
	var hours = datetime.hours;
	var minutes = datetime.minutes;
	var month = datetime.month;
	var nanos = datetime.nanos;
	var seconds = datetime.seconds;
	var time = datetime.time;
	var timezoneOffset = datetime.timezoneOffset;
	var year = datetime.year;
	
	var yyyy = 1900 + year;
	var mm = $.lpad(month,2,"0");
	var dd =  $.lpad(date,2,"0");
	
	var hh =  $.lpad(hours,2,"0");
	var mi =  $.lpad(minutes,2,"0");
	var ss =  $.lpad(seconds,2,"0");
	
	return  yyyy+"-"+mm+"-"+dd + " "+hh+":"+mi+":"+ss; 
	
}

$.transMantoZero = function(money)
{
	var zeroMoney="0.00";
	if(money==null||money==""){
		return zeroMoney;
	}
}