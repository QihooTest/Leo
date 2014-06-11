接口自动化测试框架Leo使用的实例

mvn test命令执行 或者在eclipse中执行Entry 

1.0.3 更新内容：
＝＝＝＝＝＝＝＝＝＝＝＝＝＝
pom.xml中增加邮件包依赖;
增加https请求处理（支持证书认证与非证书认证两种方式）
修改解析xml时一个key对应多个值时，实际结果值与预期结果值无法进行比较（key=value1^valu2）
CompareResult.trimActres 修改为public类型提供业务处理使用;
修改代理默认值bug;