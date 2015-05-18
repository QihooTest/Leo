package com.github.qihootest.leo.ift.core;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class CompareResultTest {
	CompareResult compResult;
	
	@BeforeTest
	public void beforeTest() {  
		compResult = new CompareResult();
	}
	
	@DataProvider(name = "exp01")
	public Object[][] createData01(){
		Object[][] retObjArr={{"views=18",true,"测试点：key=value预期结果与实际结果相等"},{"total_cost=10.01",false,"测试点：key=value预期结果与实际结果不相等"}};
		return retObjArr;
	}
	
	@Test(description="普通的数据比较key=value",dataProvider = "exp01")
	public void CompareStrTest01(String expValue,boolean expResult,String mssage){
		String actValue = "{\"errno\":0,\"data\":{\"clicks\":\"8\",\"views\":\"18\",\"total_cost\":\"10.00\",\"ad_plan_id\":\"2\"}}";
		boolean actres = compResult.getCompareResult(expValue, actValue,2);
		org.testng.Assert.assertTrue(expResult==actres, mssage);
	}
	
	@DataProvider(name = "exp02")
	public Object[][] createData02(){
		Object[][] retObjArr={{"errno=0&clicks=8&total_cost=10.00",true,"测试点：多个键值对，且实际结果与预期结果相等"},{"errno=0&clicks=9&total_cost=10.00",false,"测试点：多个键值对，实际结果与预期结果不相同"}};
		return retObjArr;
	}
	
	@Test(description="支持多个键值对的比对",dataProvider="exp02")
	public void CompareStrTest02(String expValue,boolean expResult,String msg){
		String actValue = "{\"errno\":0,\"data\":{\"clicks\":\"8\",\"views\":\"18\",\"total_cost\":\"10.00\",\"ad_plan_id\":\"2\"}}";
		boolean actres = compResult.getCompareResult(expValue, actValue,2);
		org.testng.Assert.assertTrue(expResult==actres, msg);
	}
	
	@DataProvider(name="exp03")
	public Object[][] createData03(){
		Object[][] retObjArr={{"errno=1#2#0",true,"测试点：预期结果与实际结果相等"},{"ckicks=7#9",false,"测试点：预期结果与实际结果不同"}};
		return retObjArr;
	}
	
	@Test(description="支持一个key可能对应多个值",dataProvider="exp03")
	public void CompareStrTest03(String expValue,boolean expResult,String message){
		String actValue = "{\"errno\":0,\"data\":{\"clicks\":\"8\",\"views\":\"18\",\"total_cost\":\"10.00\",\"ad_plan_id\":\"2\"}}";
		boolean actres = compResult.getCompareResult(expValue, actValue,2);
		org.testng.Assert.assertTrue(expResult==actres, message);
	}
	
	@DataProvider(name="exp04")
	public Object[][] createData04(){
//		Object[][] retObjArr={{"item=3049732936^3049732168^3049738568^3083317320^3989263944",true},{"item=3049732936^3049732168",true}};
		Object[][] retObjArr={{"item=[3049732936,3049732168,3049738568,3083317320,3989263944]",true,"测试点：对实际结果中全部值进行匹配，预期结果与实际结果相等"},
				{"item=[3049732936,3049732168]",false,"测试点：预期结果中的值不全，预期结果与实际结果不符"},{"item=3049732168",true,"测试点：预期结果中只单个包含，预期结果与实际结果相同"}};
		return retObjArr;
	}
	
	@Test(description="xml文件存在一个key对应多个值",dataProvider="exp04")
	public void CompareStrTest04(String expValue,boolean expResult,String msg){
		String actValue = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> <creative_getIdListByGroupId_response> <creativeIdList list=\"true\"> "
				+ "<item>3049732936</item> <item>3049732168</item> <item>3049738568</item> <item>3083317320</item> <item>3989263944</item> "
				+ "</creativeIdList> </creative_getIdListByGroupId_response>";
		boolean actres = compResult.getCompareResult(expValue,actValue,2);
		org.testng.Assert.assertTrue(expResult==actres, msg);
	}
	
	@DataProvider(name="exp05")
	public Object[][] createData05(){
		Object[][] retObjArr={{"clicks=int",true,"测试点：预期结果为int，符合要求"},{"errno=int",false,"测试点:预期结果为int但实际结果为0，0不包含在int范围内"}};
		return retObjArr;
	}
	
	
	@Test(description="预期结果中0不包含在",dataProvider="exp05")
	public void CompareStrTest05(String expValue,boolean expResult,String msg){
		String actValue = "{\"errno\":0,\"data\":[{\"clicks\":\"8\",\"views\":\"18\",\"total_cost\":\"10.00\",\"ad_plan_id\":\"2\"},"
				+ "{\"clicks\":\"7\",\"views\":\"16\",\"total_cost\":\"9.00\",\"ad_plan_id\":\"1\"},"
				+ "{\"clicks\":\"9\",\"views\":\"19\",\"total_cost\":\"10.00\",\"ad_plan_id\":\"3\"}]}";
		boolean actres = compResult.getCompareResult(expValue, actValue,2);
		org.testng.Assert.assertTrue(expResult==actres);
	}
	
	@DataProvider(name = "exp10")  
    public Object[][] createData() {  
        Object[][] retObjArr = { { "clicks=[8,7,9]", true ,"测试点：json数组完全匹配"},  
               { "clicks=[8,9,8]", false,"测试点:json数组中部分值错误" }, { "click=[8,7]", false ,"测试点：json预期结果必须完全包含实际结果的值"},  
                { "clicks=9", true,"" } };  
        return (retObjArr);  
    } 
	
	@Test(description="实际结果为json数组，预期结果为数组",dataProvider = "exp10")
	public void CompareStrTest10(String expValue,boolean expResult,String msg){
//		String expValue = "clicks=[8,7,9]";  //预期结果
		String actValue = "{\"errno\":0,\"data\":[{\"clicks\":\"8\",\"views\":\"18\",\"total_cost\":\"10.00\",\"ad_plan_id\":\"2\"},"
				+ "{\"clicks\":\"7\",\"views\":\"16\",\"total_cost\":\"9.00\",\"ad_plan_id\":\"1\"},"
				+ "{\"clicks\":\"9\",\"views\":\"19\",\"total_cost\":\"10.00\",\"ad_plan_id\":\"3\"}]}";
		boolean actres =  compResult.getCompareResult(expValue,actValue,2); //实际结果

		org.testng.Assert.assertTrue(expResult==actres, "能正确匹配预期结果中的数组");
	}
	

}
