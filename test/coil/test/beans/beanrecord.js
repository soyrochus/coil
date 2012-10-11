
var BeanRecord = Packages.coil.test.beans.BeanRecord;

var initImpl = function(data, x, y){
    
   return  {
	
       getData: function(){
           return data;
       },
	   getX: function(){
           return x;
       },
       getY: function(){
           return y;
       }
   };
};

var beanrecord = new BeanRecord(initImpl("Bean from Javascript", 10, 10.001));

//return from script
beanrecord;

