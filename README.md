FNLP (formerly FudanNLP)
====

介绍(Introduction)
-----------------------------------  
FNLP主要是为中文自然语言处理而开发的工具包，也包含为实现这些任务的机器学习算法和数据集。
本工具包及其包含数据集使用LGPL3.0许可证。

FNLP is developed for Chinese natural language processing (NLP), which also includes some machine learning algorithms and [DataSet data sets] to achieve the NLP tasks. FudanNLP is distributed under LGPL3.0.


If you're new to FNLP, check out the [Quick Start (使用说明)](http://www.fnlp.org/fnlp-intro) page, or [Java-docs](https://fudannlp.googlecode.com/svn/FudanNLP-1.5-API/java-docs/index.html).

原FudanNLP项目地址：http://code.google.com/p/fudannlp 

功能(Functions)
----
		信息检索： 文本分类 新闻聚类
		中文处理： 中文分词 词性标注 实体名识别 关键词抽取 依存句法分析 时间短语识别
		结构化学习： 在线学习 层次分类 聚类


[ChangeLog 更新日志(ChangeLog)]  
[性能测试(Benchmark)] (Benchmark)
[开发计划(Development Plan)] (DevPlan)
[开发人员列表(Developers)](People)

Demos
----
你可以通过试用下面的网站来测试部分功能。
You can also use the following site to check the partial functionality.
[Demo Website(演示网站)](http://jkx.fudan.edu.cn/nlp)


有遇到FNLP不能处理的例子，请到这里提交: [协同数据收集](http://code.google.com/p/fudannlp/wiki/CollaborativeCollection)。

有问题请查看[FAQ](http://www.fnlp.org/fnlp-faq)或到 QQ群（253541693）讨论。



使用(Usages)
----

[FNLP入门教程](https://github.com/xpqiu/fnlp/wiki)

除了源码文件，还需要下载FNLP模型文件。由于模型文件较大，不便于存放在源码库之中，请至[Release](https://github.com/xpqiu/fnlp/releases)页面下载，并将模型文件放在“models”目录。

* seg.m 分词模型
* pos.m 词性标注模型
* dep.m 依存句法分析模型

欢迎大家提供非Java语言的接口。

引用(Citation)
----
If you would like to acknowledge our efforts, please cite the following paper.
如果我们的工作对您有帮助，请引用下面论文。

		Xipeng Qiu, Qi Zhang and Xuanjing Huang, FudanNLP: A Toolkit for Chinese Natural Language Processing, In Proceedings of Annual Meeting of the Association for Computational Linguistics (ACL), 2013.*


		@INPROCEEDINGS{Qiu:2013,
		author = {Xipeng Qiu and Qi Zhang and Xuanjing Huang},
		title = {FudanNLP: A Toolkit for Chinese Natural Language Processing},
		booktitle = {Proceedings of Annual Meeting of the Association for Computational Linguistics},
		year = {2013},
		}

在[这里](http://jkx.fudan.edu.cn/~xpqiu/) 或  [DBLP](http://scholar.google.com/citations?sortby=pubdate&hl=en&user=Pq4Yp_kAAAAJ&view_op=list_works Google Scholar] 或 [http://www.informatik.uni-trier.de/~ley/pers/hd/q/Qiu:Xipeng.html) 可以找到更多的相关论文。

We used [JProfiler](http://www.ej-technologies.com/products/jprofiler/overview.html ) to help optimize the code.

本网站（或页面）的文字允许在CC-BY-SA 3.0协议和GNU自由文档许可证下修改和再使用。