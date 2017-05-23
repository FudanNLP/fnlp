[TOC]

# FNLP中文分词详解

## 模型介绍

我们使用基于机器学习的中文自然语言文本处理的开发工具包[FNLP](https://github.com/FudanNLP/fnlp)训练分词模型，在选取合适的特征模板后，得到以下分词模型:

[模型下载地址](https://pan.baidu.com/s/1D7CVc#list/path=%2Ffnlp网盘镜像%2F分词单独模型%2F分词模型&parentPath=%2F) 

1. msr_model
2. pku_model
3. weibo_model

|       | Precision | RECALL | F     | model_size |
| ----- | --------- | ------ | ----- | ---------- |
| Weibo | 94.02     | 94.59  | 94.30 | 13m        |
| PKU   | 95.09     | 94.17  | 94.62 | 10m        |
| MSR   | 96.84     | 96.61  | 96.73 | 12m        |

PKU和MSR语料是SIGHAN于2005年组织的中文分词比赛所用的数据集，内容为新闻语料，遣词造句比较严谨；微博语料取自多个领域新浪微博公众号，网络新词多，词句用法更生活。

## 分词

### 下载和编译FNLP

直接下载编译好的jar包或自行编译

#### 直接下载

压缩包下载地址（[app.zip](http://url.cn/49V4g6c)）

#### 自行编译

##### MacOS or Ubuntu

详见[教程](https://github.com/FudanNLP/fnlp/wiki/quicktutorial)，按照教程完成至1.2节

##### Windows

windows下maven的安装和环境变量设置略不同，详见下面

1. 下载maven压缩包, [apache-maven-3.5.0-bin.zip](http://maven.apache.org/download.cgi)
2. 将其解压到一个固定的文件夹中，如C:\tools目录下
3. 修改环境变量。打开系统属性面板（右击我的电脑->属性->高级系统设置），然后点击“环境变量”->“新建”->然后点击"环境变量" ->"新建"->输入"M2\_HOME"和Maven解压后的根目录路径（我解压到C:\tools下所以完整的路径就是C:\tools\apache-maven-3.5.0），然后点击确定，再然后找到名为Path的系统变量，单击选中后点击"编辑"，将 %M2\_HOME%\bin; 添加到变量值的开头（注意最后的分号也是要添加的）。

其余内容同教程相同，按照教程完成至1.2节

### 命令行调用

##### MacOS or Ubuntu

```
java -Xmx1024m -Dfile.encoding=UTF-8 -classpath "fnlp-core/target/fnlp-core-2.1-SNAPSHOT.jar:libs/trove4j-3.0.3.jar:libs/commons-cli-1.2.jar" org.fnlp.nlp.cn.tag.CWSTagger -s models/seg.m "自然语言是人类交流和思维的主要工具，是人类智慧的结晶。"
```

or

```
java -Xmx1024m -Dfile.encoding=UTF-8 -classpath "fnlp-core/target/fnlp-core-2.1-SNAPSHOT.jar:libs/trove4j-3.0.3.jar:libs/commons-cli-1.2.jar" org.fnlp.nlp.cn.tag.CWSTagger -f models/seg.m input.txt output.txt
```

##### Windows

```
java -Xmx1024m -Dfile.encoding=UTF-8 -classpath "fnlp-core/target/fnlp-core-2.1-SNAPSHOT.jar;libs/trove4j-3.0.3.jar;libs/commons-cli-1.2.jar" org.fnlp.nlp.cn.tag.CWSTagger -s models/seg.m "自然语言是人类交流和思维的主要工具，是人类智慧的结晶。"
```

or

```
java -Xmx1024m -Dfile.encoding=UTF-8 -classpath "fnlp-core/target/fnlp-core-2.1-SNAPSHOT.jar;libs/trove4j-3.0.3.jar;libs/commons-cli-1.2.jar" org.fnlp.nlp.cn.tag.CWSTagger -f models/seg.m input.txt output.txt
```

windows cmd 主要变动为将classpath中的“:”改为**“;”**

##### 执行结果如下

```
自然 语言 是 人类 交流 和 思维 的 主要 工具 ， 是 人类 智慧 的 结晶 。
```

or

```
生成output.txt
```

##### 参数解析

参数“-Xmx1024m”设置Java虚拟机的可用内存为1024M。FNLP载入语言模型所需内存较大，因此可以利用此参数修改可用内存量。

参数“-classpath ...”载入Jar文件。因此在引号内依次写下fnlp-core、trove、commons-cli的Jar包路径，使用英文冒号（macos or ubuntu）或者分号（windows）分隔文件名。

参数“org.fnlp.nlp.cn.tag.CWSTagger”指定了本次调用的类名，表示调用中文分词器CWSTagger。而后续的参数是根据所调用的类确定的。详见如下：

1. java org.fnlp.tag.CWSTagger -f model_file input_file output_file

    分词输入为文件， 文件内每行是一个待分的句子

2. java org.fnlp.tag.CWSTagger -s model_file string_to_segment 

   分词输入为string

model_file 即为**模型介绍**中生成的分词模型文件

### 接口调用

##### 添加库引用

详见[教程](https://github.com/FudanNLP/fnlp/wiki/quicktutorial)，按照教程完成第1.3节

##### 调用分词接口

FNLP提供了一系列中文处理工具，其中中文分词、词性标注、实体名识别等基础功能已经封装在工厂类CNFactory之中。CNFactory位于org.fnlp.nlp.cn包之中，经过初始化后就可以使用其提供的全部功能：

```java
package wordseg;
import org.fnlp.nlp.cn.CNFactory;
import org.fnlp.nlp.cn.CNFactory.Models;
public class test {
	public static void main(String[] args) throws Exception {
		// 创建中文处理工厂对象，并使用“texts/msr”目录下的模型文件初始化, 选择模型文件为Models.SEG
	    CNFactory factory = CNFactory.getInstance("texts/msr", Models.SEG);
	    // 使用分词器对中文句子进行分词，得到分词结果
	    String[] words = factory.seg("关注自然语言处理、语音识别、深度学习等方向的前沿技术和业界动态。");
	    // 打印分词结果
	    for(String word : words) {
	        System.out.print(word + " ");
	    }
	     System.out.println();
	}
}
```

**注意**：models目录下应包含分词模型文件， 文件命名为**seg.m**

##### 输出

```
关注 自然 语言 处理 、 语音 识别 、 深度 学习 等 方向 的 前沿 技术 和 业界 动态 。
```

