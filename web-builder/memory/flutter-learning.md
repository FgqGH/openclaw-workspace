# Flutter 学习笔记

> 学习时间：2026-04-16
> 状态：✅ 基础已覆盖

## 概述

- **开发者**：Google
- **语言**：Dart
- **核心**：自绘引擎，绕过原生控件
- **平台**：iOS、Android、Web、Desktop（Windows/macOS/Linux）

## Dart 语言基础

### 变量与类型
```dart
// 类型推断
var name = 'Flutter';        // String
int count = 0;                // 整型
double price = 19.99;        // 浮点
bool isActive = true;        // 布尔

// 空安全
String? nullable;            // 可空类型
String nonNullable = 'Hello'; // 非空类型（需初始化或 late 声明）
late String lateInit;        // 延迟初始化
```

### 函数
```dart
// 箭头函数
int add(int a, int b) => a + b;

// 命名参数（常用）
void greet({required String name, String? greeting}) {
  print('${greeting ?? 'Hello'}, $name');
}
greet(name: 'John', greeting: 'Hi');

// 可选参数
void greet(String name, [String? greeting]) { ... }
```

### 类与构造
```dart
class Person {
  final String name;
  final int age;
  
  // 初始化列表
  Person(this.name, this.age);
  
  // 命名构造
  Person.guest() : name = 'Guest', age = 0;
  
  // get set
  String get info => '$name, $age years old';
}

// 继承
class Student extends Person {
  final String school;
  
  Student(super.name, super.age, this.school);
}
```

### 异步
```dart
Future<String> fetchData() async {
  await Future.delayed(Duration(seconds: 1));
  return 'Data loaded';
}

// await 使用
String data = await fetchData();

// Stream
Stream<int> countStream() async* {
  for (int i = 1; i <= 5; i++) {
    await Future.delayed(Duration(seconds: 1));
    yield i;
  }
}
```

---

## Flutter 核心概念

### Widget 体系

**一切皆 Widget**

| 分类 | 示例 | 特点 |
|------|------|------|
| 基础 | `Text`, `Image`, `Icon` | 原子组件 |
| 布局 | `Container`, `Row`, `Column`, `Flex` | 控制排列 |
| 列表 | `ListView`, `GridView`, `SliverList` | 滚动容器 |
| 导航 | `Navigator`, `Router` | 页面切换 |
| 交互 | `GestureDetector`, `InkWell`, `Draggable` | 手势响应 |
| 动画 | `AnimatedContainer`, `Hero`, `Rive` | 动效 |

**StatelessWidget（无状态）**
```dart
class MyWidget extends StatelessWidget {
  final String title;
  
  const MyWidget({super.key, required this.title});
  
  @override
  Widget build(BuildContext context) {
    return Text(title);
  }
}
```

**StatefulWidget（有状态）**
```dart
class Counter extends StatefulWidget {
  @override
  State<Counter> createState() => _CounterState();
}

class _CounterState extends State<Counter> {
  int _count = 0;
  
  void _increment() {
    setState(() {
      _count++;
    });
  }
  
  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Text('Count: $_count'),
        ElevatedButton(
          onPressed: _increment,
          child: Text('Add'),
        ),
      ],
    );
  }
}
```

---

## 布局系统

### 线性布局
```dart
// 水平
Row(
  mainAxisAlignment: MainAxisAlignment.spaceBetween,
  children: [Text('A'), Text('B')],
)

// 垂直
Column(
  crossAxisAlignment: CrossAxisAlignment.start,
  children: [Text('A'), Text('B')],
)
```

### 弹性布局
```dart
// Flexible + Expanded
Row(
  children: [
    Expanded(flex: 2, child: Container(color: Colors.red)),
    Expanded(flex: 1, child: Container(color: Colors.blue)),
  ],
)
```

### Stack（层叠）
```dart
Stack(
  children: [
    Container(color: Colors.grey),
    Positioned(
      top: 10,
      right: 10,
      child: Icon(Icons.star),
    ),
  ],
)
```

---

## 导航

### 基础导航
```dart
// 跳转
Navigator.push(
  context,
  MaterialPageRoute(builder: (context) => DetailPage()),
);

// 返回
Navigator.pop(context);

// 替换
Navigator.pushReplacement(context, MaterialPageRoute(...));

// 路由命名（需配置 routes）
Navigator.pushNamed(context, '/detail');
```

### 路由配置
```dart
MaterialApp(
  initialRoute: '/',
  routes: {
    '/': (context) => HomePage(),
    '/detail': (context) => DetailPage(),
  },
);
```

---

## 状态管理

### setState（局部状态）
适用于简单 UI 状态

### Provider（官方推荐入门）
```dart
// 定义
class CounterModel extends ChangeNotifier {
  int _count = 0;
  int get count => _count;
  
  void increment() {
    _count++;
    notifyListeners();
  }
}

// 使用
ChangeNotifierProvider(
  create: (_) => CounterModel(),
  child: MyApp(),
)

// 获取
Consumer<CounterModel>(
  builder: (context, counter, child) {
    return Text('${counter.count}');
  },
)

// 或
final counter = context.read<CounterModel>();
```

### Riverpod（更现代）
```dart
// 依赖注入
final counterProvider = StateProvider<int>((ref) => 0);

// 读取
final count = ref.watch(counterProvider);
```

### BLoC（商业级）
```dart
// Event
abstract class CounterEvent {}
class Increment extends CounterEvent {}

// State
class CounterState { final int count; CounterState(this.count); }

// BLoC
class CounterBloc extends Bloc<CounterEvent, CounterState> {
  CounterBloc() : super(CounterState(0)) {
    on<Increment>((event, emit) {
      emit(CounterState(state.count + 1));
    });
  }
}
```

---

## 网络请求

### http 包
```dart
import 'package:http/http.dart' as http;

final response = await http.get(Uri.parse('https://api.example.com/data'));
final data = jsonDecode(response.body);
```

### dio（更强大）
```dart
final dio = Dio();
final response = await dio.get('https://api.example.com/data');

// 拦截器
dio.interceptors.add(LogInterceptor(requestBody: true));
```

---

## 常用命令

```bash
# 创建项目
flutter create my_app
flutter create --platforms=ios,android my_app

# 运行
flutter run
flutter run -d chrome

# 构建
flutter build apk --release
flutter build ios --release
flutter build web

# 依赖
flutter pub add provider
flutter pub get
flutter pub upgrade

# 分析
flutter analyze
dart analyze
```

---

## pubspec.yaml 结构

```yaml
name: my_app
description: A new Flutter project.

environment:
  sdk: '>=3.0.0 <4.0.0'

dependencies:
  flutter:
    sdk: flutter
  provider: ^6.0.0
  dio: ^5.0.0

dev_dependencies:
  flutter_test:
    sdk: flutter
  flutter_lints: ^3.0.0
```

---

## 资源链接

- 官网：https://flutter.dev
- 文档：https://docs.flutter.dev
- Dart：https://dart.dev
- Pub.dev（包仓库）：https://pub.dev

---

## 待深入

- [ ] 动画系统
- [ ] 自定义绘制（CustomPainter）
- [ ] Platform Channels（原生通信）
- [ ] 路由进阶（go_router）
- [ ] 状态管理进阶（Riverpod/BLoC）
- [ ] 性能优化
- [ ] 单元测试 & 集成测试
