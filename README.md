# GratuityView
仿虎扑体育NBA文字直播打赏控件

</br>
##前言
先看下虎扑原型：

![原型.png](http://upload-images.jianshu.io/upload_images/2355808-52772505711f54ff.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/300)

</br>
我们实现的效果图:
![色彩效果.gif](http://upload-images.jianshu.io/upload_images/2355808-e9bf0ac12846db1b.gif?imageMogr2/auto-orient/strip)

##使用
直接在xml文件内引用
```
<com.dengzq.gratuityview.GratuityView.GratuityView
        app:animationDuration="800"
        app:baseTextColor="#8E388E"
        app:childTextColor="#FFFFFF"
        app:childgroundColor="#B4EEB4"
        android:id="@+id/gratuity_view"
        android:layout_width="match_parent"
        android:layout_height="60dp"
        android:layout_alignParentRight="true"
        android:layout_centerVertical="true"/>
```
</br>
##相关属性
| 属性    | 描述    |
|--------|:--------:|
|     baseTextSize   |   大圆文字大小     |
|     baseTextColor   |   大圆文字颜色     |
|     basegroundColor   |   大圆背景颜色     |
|     childTextSize   |   小圆文字大小     |
|     childTextColor   |   小圆文字颜色     |
|     childgroundColor   |   小圆背景颜色     |
|     animationDuration   |   动画时长    |
|     animationBackground   |   展开背景框颜色    |
|     childCount   |   小圆数目    |
|     autoCollapse   |   是否自动收缩    |
|     collapseDelay   |   收缩延时    |

代码内的设置
```
gview.setBaseText("Android","dengzq");     //设置大圆文本；双行文本
gview.setChildText("x1",1);                //设置index=1的小圆的文本
gview.setChildText("x1000",4);             //设置index=4的小圆文本
gview.setAnimDuration(800);                //设置动画时长
gview.setAutoCollapse(true);               //设置是否自动收缩，默认为true
gview.setChildCount(5);                    //设置小圆数目
gview.setChildgroundColor(0xFFEE6E50,0);   //设置所有小圆背景色
gview.setOnItemClickListener(new onItemClickListener())   //设置小圆点击事件
```
##end~
喜欢的可以赏个star哦~
