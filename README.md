# GuaJiangView

A View which can accomplish funtion of scratching pad

可以实现刮奖功能的自定义View

[中文文档](https://www.cnblogs.com/kexing/p/9517867.html)
## Picture
![](https://images2018.cnblogs.com/blog/1210268/201808/1210268-20180822154915687-2057438815.gif)

## Useage
1. **Add it in your root build.gradle at the end of repositories:**

		allprojects {
			repositories {
				...
				maven { url 'https://jitpack.io' }
			}
		}
2. **Add the dependency**

		dependencies {
		    compile 'com.github.Stars-One:GuaJiangViewDemo:v1.2'
		}
3. **use it in your xml**

![](https://images2018.cnblogs.com/blog/1210268/201808/1210268-20180822144920781-533871503.png)

4. **set its listener of  complete**

![](https://images2018.cnblogs.com/blog/1210268/201808/1210268-20180822145603405-31856029.png)

## Other
| property | description | default |
| - | :-: | -: |
| text| the showing text  | null |
| textSize| the size of text | 16 |
| textColor | the color of text | black |
| PaintSize| the width of erasure effect | 10 |
| messageBackground | the background or the background color of layer of the message | null |
| isDrawText | chose show text or show picture  | true |
|cover | the picture or color of cover layer | null|
| clearFlag  |  when clear greater than clearFlag,then clear cover layer | 60 percent|
