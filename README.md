# AnkoExample
A example to apply Anko in Android

[Anko](https://github.com/JetBrains/anko) home page

Anko for Android
===========
>[Anko](https://github.com/JetBrains/anko) 是一个使开发Android应用更简单更快捷的库,Anko使你的代码简洁易懂, 使开发者不用再在意Android SDK对Java版本的限制(目前还不支持Java8  =。= ).

 Anko版本的 `hello world` :
```kotlin
verticalLayout {
    val name = editText()
    button("Say Hello") {
        onClick { toast("Hello, ${name.text}!") }
    }
}
```
上面的代码创建了一个Button,放在 `LinearLayout` 内, 并为其设置了一个点击监听器`OnClickListener` .

![](http://file.bmob.cn/M02/32/C1/oYYBAFYnZ7GAHENVAATT_iv8nqY107.gif) 

上面是一个DSL(Domain Specific Language),使用的是 [Kotlin](http://kotlinlang.org)语言.
>DSL，即 Domain Specific Language，领域相关语言。什么是 DSL，说白了它就是某个行业中的行话。

[TOC]

## Why Anko?

### 为啥 DSL?

平时开发android, UI写在xml中,这就导致了下面的几个问题 :
* It is not typesafe
* It is not null-safe
* It forces you to write almost *the same code* for every layout you make 
* XML is parsed on the device wasting CPU time and battery 渲染xml为对象过程耗时耗电
* Most of all, it allows no code reuse. 大部分不能重用

但是全部只在代码中写UI,这很难,不仅代码丑,而且冗余难维护,下面是 **Ktolin**版本的(Java甚至更长):

```kotlin
val act = this
val layout = LinearLayout(act)
layout.setOrientation(LinearLayout.VERTICAL)
val name = EditText(act)
val button = Button(act)
button.setText("Say Hello")
button.setOnClickListener {
    Toast.makeText(act, "Hello, ${name.getText()}!", Toast.LENGTH_SHORT).show()  
}
layout.addView(name)
layout.addView(button)
```
DSL 就不一样类,相同的逻辑,但简洁易懂, 易于编写而且没有运行开销(runtime overhead)看下面的代码:

```kotlin
verticalLayout {
    val name = editText()
    button("Say Hello") {
        onClick { toast("Hello, ${name.text}!") }
    }
}
```

### 为啥不用 Scaloid?

[Scaloid](https://github.com/pocorall/scaloid) 是一个类似与 Scala 的库, 有很多非常酷的特性可供 Scala 开发者使用. Anko主要是针对 Java 和 Kotlin developers.


### 兼容已有的代码

不需要用Anko重写所有的UI, 你可以保留原有的Java代码. 此外, 如果你想写一个 **Kotlin**的activity类并且由于某些需求需要使用 inflate来渲染xml, 你完全可以按照原来的写法:
```kotlin
// Same as findViewById(), simpler to use
val name = find<TextView>(R.id.name)
name.hint = "Enter your name"
name.onClick { /*do something*/ }
```

### 工作原理

There is no :tophat:. Anko 由一些 **Kotlin**的 [扩展函数和属性](http://kotlinlang.org/docs/reference/extensions.html),被设置成**类型安全**(*type-safe builders*)的, [under Type Safe Builders](http://kotlinlang.org/docs/reference/type-safe-builders.html).

他们繁琐的手工编写所有这些扩展, 使用Android SDK的源码中的 *android.jar* 文件自动生成

### 可扩展吗?

答案是: **yes**.
例如. 你可能想使用 `MapView` 在DSL中.你可以编写下面的代码(kotlin文件中),然后就可已到处使用了
```kotlin
public inline fun ViewManager.mapView() = mapView {}
public inline fun ViewManager.mapView(init: MapView.() -> Unit): MapView {
    return ankoView({ MapView(it) }, init)
}
```

``{ MapView(it) }`` 是你自定义View的一个工厂方法`View`. 接受一个 `Context` .


```kotlin
frameLayout {
    val mapView = mapView().lparams(width = matchParent)
}
```
如果你想创建一个 顶级的 DSL,看[这里Extending Anko](https://github.com/JetBrains/anko/blob/master/doc/ADVANCED.md#extending-anko).

### 使用 Gradle

这里有个例子 [template project](https://github.com/yanex/anko-template-project) 展示类如果在Android中Gradle配置.

基本上,你只需要配置  `repository` 和一个 `compile dependency`:

```gradle
dependencies {
    compile 'org.jetbrains.anko:anko-sdk15:0.7.1' // sdk19, sdk21, sdk23 are also available
    compile 'org.jetbrains.anko:anko-support-v4:0.7.1' // In case you need support.v4 bindings
}
```

### 当作 Jar library使用

加入你的项目不是基于Gradl, 不需要配置 Maven. 只要添加[这里](https://github.com/JetBrains/anko/releases) 的jar包即可.

### 编译 Anko

如何编译看[under Building](https://github.com/JetBrains/anko/blob/master/doc/BUILDING.md).

## 理解 Anko

Anko 是使用 **Kotlin**语言编写的. 
如果不熟悉**Kotlin**看 [kotlinlang.org](http://kotlinlang.org/docs/reference/). 
**Kotlin**与Java很类似,所以很容易学.

### 基础

Anko中, 你不需要继承其他奇怪的类,只要标准的`Activity`, `Fragment`, `FragmentActivity` 或者其他任意的类

首先, 在使用Anko的DSL的类中导入 `org.jetbrains.anko.*` .

DSL 可以在 `onCreate()`中使用:

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super<Activity>.onCreate(savedInstanceState)
    
    verticalLayout {
        padding = dip(30)
        editText {
            hint = "Name"
            textSize = 24f
        }
        editText {
            hint = "Password"
            textSize = 24f
        }
        button("Login") {
            textSize = 26f
        }
    }
}
```

不需要显示的调用 `setContentView(R.layout.something)`,  Anko 自动为Activity（只会对Activity）进行 set content view 

`padding`, `hint` 和 `textSize` 是 [扩展属性](http://kotlinlang.org/docs/reference/extensions.html#extension-properties). 大多数 `View` 具有这些属性，允许使用`text = "Some text"` 代替 `setText("Some text")`.

`verticalLayout` (一个竖直方向的 `LinearLayout`), `editText` 和 `button` are
[扩展函数](http://kotlinlang.org/docs/reference/extensions.html). 这些函数存在与ANdroid 框架中的大部View中,  `Activities`, `Fragments` ( `android.support` 包中的) 甚至 `Context`同样适用.

如果有一个 `Context` 实例, 可以写出下面的DSL结构:

```kotlin
val name = with(myContext) {
    editText {
        hint = "Name"
    }
}
```

变量 `name` 成为了 `EditText`类型.

### Helper 方法

你可能注意到了，前面章节中  `button` 方法接了一个字符串参数，这样的Helper方法同样使用与 `TextView`, `EditText`, `Button` ， `ImageView`.

如果你不需要 `View` 其他的属性，你可以省略 `{}` 直接写 `button("Ok")` 或只有 `button()`:

```kotlin
verticalLayout {
    button("Ok")
    button("Cancel")
}
```

### Layouts 和 LayoutParams

在父布局中布局控件可能需要使用 `LayoutParams`. xml中长这样:

```xml
<ImageView 
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android_layout_marginLeft="5dip"
    android_layout_marginTop="10dip"
    android:src="@drawable/something" />
```

Anko中, 在`View`的后面使用 `lparams`来实现类似与xml的 `LayoutParams`。

```kotlin
linearLayout {
    button("Login") {
        textSize = 26f
    }.lparams(width = wrapContent) {
        horizontalMargin = dip(5)
        topMargin = dip(10)
    }
}
```

如果指定了 `lparams`,但是没有指定 `width` 或者 `height`, 默认是 `WRAP_CONTENT`.但是你可以自己通过使用[named arguments](http://kotlinlang.org/docs/reference/functions.html#named-arguments)指定.


注意下面一些方便的属性:

- `horizontalMargin` 同时设置 left 和 right margins, 
- `verticalMargin` 同时设置   top 和 bottom  
- `margin` 同时设置4个方向的 margins.

**注意**  `lparams` 的使用在不同的布局中是不同的, 例如在 `RelativeLayout`中:

```kotlin
val ID_OK = 1

relativeLayout {
    button("Ok") {
        id = ID_OK
    }.lparams { alignParentTop() }
  
    button("Cancel").lparams { below(ID_OK) }
}
```

### Listeners

设置listeners:

```kotlin
button("Login") {
    onClick {
        login(name, password)
    }
}
```

下面的效果一样:

```kotlin
button.setOnClickListener(object : OnClickListener {
    override fun onClick(v: View) {
        login(name, password)
    }
})
```

当一个Listener有多个方法时，Anko就显得很方便类. 看下面的代码（没有使用Anko）:

```kotlin
seekBar.setOnSeekBarChangeListener(object: OnSeekBarChangeListener {
    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        // Something
    }
    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        // Just an empty method
    }
    override fun onStopTrackingTouch(seekBar: SeekBar) {
        // Another empty method
    }
})
```

使用了Anko:

```kotlin
seekBar {
    onSeekBarChangeListener {
        onProgressChanged { seekBar, progress, fromUser ->
            // Something
        }
    }
}
```

如果你同时设置了`onProgressChanged` 和 `onStartTrackingTouch`  , 两个方法将被合并. 对于多个相同的方法，最后的一个有效.

### Resources, Colors 和 Dimensions

#### Using resource identifiers

前面的所有例子直接使用的 Java的字符串，但是大多数时候字符串都是放在  `res/values/` 目录下，并且是运行时调用的，例如 `getString(R.string.login)`.

幸运的是，Anko中可以使用以下两个 helper方法 (`button(R.string.login)`) 和 (`button { textResource = R.string.login }`).

**注意**，这些属性不是 `text`, `hint`, `image`, 而是 `textResource`, `hintResource` and `imageResource`.
>Resource properties always throw `AnkoException` when read. 
 

#### Colors

两个简单的扩展函数使代码更加易懂。

Function             | Result
---------------------|--------- 
`0xff0000.opaque`    | <span style="color:#ff0000">non-transparent red</span>
`0x99.gray.opaque`   | <span style="color:#999">non-transparent #999999 gray</span>

#### Dimensions

你可以指定 dimension 的 **dip** (density-independent pixels) 或  **sp** (scale-independent pixels)值: `dip(dipValue)` 或 `sp(spValue)`. **注意** `textSize`属性默认接受**sp** (`textSize = 16f`). 使用 `px2dip` 和 `px2sp` 相互转换.

### Instance shorthands

在Activity中，有时你需要传一个 `Context`实例给一个 Android SDK中的方法，通常你会写 `this`, 如果在内部类呢?你可能写`SomeActivity.this` ，如果你使用 **Kotlin**，你只需写 `this@SomeActivity` ，
使用 Anko，你可以只写  `ctx`. `ctx`是`Activity` 和 `Service` 或者 `Fragment` (使用的 `getActivity()` )内部的一个属性. 你也可以使用`act`扩展属性获取 `Activity`实例.

### UI wrapper

开始使用Anko 之前，将 `UI` tag 作为 DSL 顶级元素:

```kotlin
UI {
    editText {
        hint = "Name"
    }
}
```

这将更易于扩展 DSL ，因为你必须声明一个函数 `ViewManager.customView`.
看这里 [Extending Anko]( https://github.com/JetBrains/anko/blob/master/doc/ADVANCED.md#extending-anko) 获取更多信息.

### Include tag

使用 `include` tag 很容易向 DSL 插入 一个 XML layout ：

```kotlin
include<View>(R.layout.something) {
    backgroundColor = Color.RED
}.lparams(width = matchParent) { margin = dip(12) }
```

通常可以使用 `lparams` ， 如果类型不是 `View`,仍然可以用 `{}`:

```kotlin
include<TextView>(R.layout.textfield) {
    text = "Hello, world!"
}
```

### Styles

Anko 支持 styling: `style` 是一个简单的函数，接受一个`View`, 效果作用于这个 `View` , 并且当这个`View` 是一个`ViewGroup` 时，可以可以递归的作用与 这个View的 child View:

```kotlin
verticalLayout {
    editText {
        hint = "Name"
    }
    editText {
        hint = "Password"
    }
}.style { view -> when(view) {
    is EditText -> view.textSize = 20f
}}
```

---

[![](http://file.bmob.cn/M02/32/C5/oYYBAFYnaXGAU36mAAA-AHHYR0Y038.png)](http://kotlinlang.org)

---
