<%@page pageEncoding="UTF-8"%>
<%@include file="/tag.jsp"%>

<head>
	<style type="text/css">
        .biaoge2 { width:100%; border:1px solid #c0de98; }
		.biaoge2 th { height:24x; line-height:24px; border:1px solid #C5D5C5; padding:0px 2px; color:#000; font-weight:normal;}
		.biaoge2 td { height:20x; line-height:20px; padding:2px 2px; border:1px solid #C5D5C5; text-align:left; background:#F4F8F4; }
	</style>
</head>
<body>
     <div style="clear:both;margin:5px;">
        <input type="button" value="返回" onclick="javascript:history.back(-1);"/>
     </div>
  <table  border="0" cellspacing="0" cellpadding="0">
  <tr> 
	<c:forEach items="${list}" var="item" varStatus="status">
    <td>
    <table  border="0" cellspacing="0" cellpadding="0" class="biaoge2" style="width:600px;">
      <tr>
       <th scope="row" width="30%">代理商名称</th>
       <td> ${item.agent_name} </td>
     </tr>
     <tr>
       <th scope="row" >代理商编号</th>
       <td> ${item.agent_no} </td>
     </tr>
    <tr>
       <th scope="row" >民生A类</th>
       <td> ${item.live_a_type} </td>
     </tr>
    <tr>
       <th scope="row" >民生B类</th>
       <td> ${item.live_b_type} </td>
     </tr>
         <tr>
       <th scope="row" >批发对公：</th>
       <td>${item.wholesale_pub_type}</td>
     </tr>
         <tr>
       <th scope="row" >批发对私封顶类</th>
       <td> ${item.wholesale_pri_cap_type} </td>
     </tr>
         <tr>
       <th scope="row" >批发对私非封顶类</th>
       <td> ${item.wholesale_pri_nocap_type}  </td>
     </tr>
         <tr>
       <th scope="row" >房地产及汽车销售类</th>
       <td> ${item.estate_car_type} </td>
     </tr>
         <tr>
       <th scope="row" >一般A类</th>
       <td > ${item.general_type}  </td>
     </tr>
     <tr>
       <th scope="row" >一般B类</th>
       <td>${item.general_b_type}</td>
     </tr>
      <tr>
       <th scope="row" >餐饮类</th>
       <td>${item.catering_type}</td>
     </tr>
      <tr>
       <th scope="row" >移小宝</th>
       <td>${item.smbox_type}</td>
     </tr>
   </table>
   </td>
</c:forEach>
    </tr>
     </table>
    <div style="clear:both;margin:5px;">
         <input type="button" value="返回" onclick="javascript:history.back(-1);"/>
    </div>
   </body>


