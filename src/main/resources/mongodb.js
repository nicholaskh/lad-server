
show dbs;
user mongo_dwz;
show collections;
db.member.find();


var document = ({
	no:"xxxxx0002",
	name:"hwj002",
	age:24,
	url:"http://blog.csdn.net/huweijun_2012/article/details/51865569",
	password:"8090987998d",
	phone:"13027981420",
	sex:0,
	status:1,
	address:"深圳市宝安区中心大道1000号"
});

db.member.insert(document);

db.member.update({ 
	no:"xxxxx0002",
	name:"hwj002"
},{
	$set:{name:"new name",age:32}
},{
	upsert:false, //upsert : 可选，这个参数的意思是，如果不存在update的记录，是否插入objNew,true为插入，默认是false，不插入。
	multi:true//multi : 可选，mongodb 默认是false,只更新找到的第一条记录，如果这个参数为true,就把按条件查出来多条记录全部更新。
});

db.member.find().pretty();

//save() 方法通过传入的文档来替换已有文档
db.member.save({
	_id:ObjectId("56064f89ade2f21f36b03136"),
	no:"xxxxx0002",
	name:"hwj002",
	age:90,
	url:"http://blog.csdn.net/huweijun_2012/article/details/51865569",
	password:"8090987998d",
	phone:"13027981420",
	sex:1,
	status:1,
	address:"深圳市宝安区中心大道1000号"
});

//pretty() 方法以格式化的方式来显示所有文档。
db.member.find().pretty();

//remove() 方法的基本语法格式如下所示：
db.member.remove({
	no:"xxxxx0002"
});

//删除所有数据
db.member.remove({});

//查询文档
db.member.find({
	no:"xxxxx0002",
	$or:[{no:"xxxxx0002"},{name:"xxxxx0002"}]
}).pretty();

//$gt >              $gte >=                 $lt <                     $lte <=        $ne <>            $or    or            $set    $in  in     $nin [not in]

db.member.find({
	age:{$gte:18,$lte:30},
	sex:1,
	$or:[{no:"xxxxx0002"},{name:"xxxxx0002"}],
	phone:{$ne:'13027981840'},
	url:{$in:["url1","url1","url1"]},
	url:{$nin:["url_00","url_99"]}
});

// select * from member where age>= 18 and age<=30 and sex=1 and (no='xxxxx0002' or name = 'xxxxx0002') and phone <> '13027981840';

db.member.find({
	$or:[{
		url:{$in:["url1","url1","url1"]},
	},{
		sex:1
	}]
});

//$mod 会将查询的值除以第一个值,若余数等于第二个给定的值则返回结果
db.member.find({
	age:{$mod:[5,1]}
});
//上面会返回age为1,6,11,16等用户.
//如果要返回非1,6,11,16这些的用户.则:
db.member.find({
	age:{$not:{$mod:[5,1]}}
});
//null 不仅仅匹配自己,而且还匹配不存在的.
//以下为:查询name为null的,而且存在的.
db.member.find({
	name:{$in:[null],$exists:true}
});

//正则
db.member.find({
	url:/www./   ///^www.$/
});


//分页
db.member.find().limit(1).skip(2).pretty();

//sort
// -1 desc(降序),1 asc(升序)
db.member.find().limit(10).skip().sort({age:-1}).pretty();

//$slice 返回数组的一个子集合
var blog = ({
	id:1222,
	title:'关于中国发展',
	author:'weijunhu',
	date:122222222222,
	comments:[
	          {
	        	  name:'joerr',
	        	  emial:'57@cc.omc',
	        	  content:'不错的文档',
	        	  score:3
	          },{
	        	  name:'boorss',
	        	  emial:'8957@cc.omc',
	        	  content:'不错的文档,go',
	        	  score:6
	          }]
});
db.blog.find(
	{
		id:1222
	},
	{
		comments:{
			$slice:-1
		}
	}
	);

//结果为:
var res = {
	id:1222,
	title:'关于中国发展',
	author:'weijunhu',
	date:122222222222,
	comments:[
	          {
	        	  name:'boorss',
	        	  emial:'8957@cc.omc',
	        	  content:'不错的文档,go',
	        	  score:6
	          }]
};

//$elemMatch
//将限定条件进行分组,仅当需要对一个内嵌文档的多个键操作时才会用到.

db.blog.find({comments:{$elemMatch:{name:"joerr",scor:{$gte:3}}}});

//MongoDB使用 ensureIndex() 方法来创建索引。
//db.COLLECTION_NAME.ensureIndex({KEY:1})
//语法中 Key 值为你要创建的索引字段，1为指定按升序创建索引，如果你想按降序来创建索引指定为-1即可。
db.blog.ensureIndex({title:1});

//内嵌文档索引
db.blog.ensureIndex({"comments.name":1});

//自定义索引名称
db.blog.ensureIndex({a:1,b:-1},{name:"alphabet"});

//唯一索引
db.member.ensureIndex({no:1},{unique:true});

//复合唯一索引


//查看是否使用了索引
db.member.find({userName:"NO698989"}).explain();









































