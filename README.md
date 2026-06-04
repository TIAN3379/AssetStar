# 资产星图 V3.0.0

资产星图是一款使用 Kotlin、Jetpack Compose 和 Room 开发的个人资产与预算管理 App。App 以“星图 / 小行星 / 星舰航行”为主要视觉语言，把个人物品管理、资产分析、预算记录和消费复盘做成更有沉浸感的移动端体验。

## V3.0.0 重点

- 新增星舰记账模块：通过底部导航中间按钮进入，支持收入、支出、分类、资金舱和历史流水记录。
- 新增预算能源系统：用户可设置每月预算，根据当月消耗比例显示不同能源核心状态。
- 新增预算预警：支持能源稳定、消耗偏快、低能源航行和超预算故障状态。
- 新增消费场景反馈：记账后飞船会从停机坪飞向对应消费/收入场所，形成视觉反馈。
- 新增资金舱管理：支持记录银行卡、支付宝、微信等资金来源的基础余额。
- 分析页升级为双页签：保留资产星图分析，新增“航行报告”预算复盘。
- 航行报告支持月份选择，展示预算、实际支出、节省/超支、分类洞察和环比趋势。
- 首页优化：移除进入首页时的扫描变暗效果，保持进入时正常亮度。
- 自动记账助手已暂时移除：相关无障碍、通知监听、悬浮窗、OCR 功能和 UI 均已删除，后续确定方向后再重新设计。

## 已有功能

- 资产新增、编辑、删除
- 资产图片本地持久保存
- 首页资产星图总览
- 物品总值与持有日均成本统计
- 分类小行星节点统计与分类详情跳转
- 资产明细搜索、筛选与排序
- 资产详情页查看完整信息
- 珍藏资产标识
- 资产资料完成度
- 高价值资产视觉强调
- 分析页资产统计
- 分类名称和图标管理
- 人民币 / 美元金额显示切换
- 实时汇率刷新
- 星舰记账手动流水记录
- 月预算设置与预算能源状态
- 资金舱余额管理
- 月度航行报告
- 自定义 App 桌面图标

## 技术栈

- Kotlin
- Jetpack Compose
- Material 3
- Room
- Coroutines / Flow
- Coil
- Lottie
- Canvas 自定义绘制

## 项目结构

- `app/src/main/java/com/example/assetstar/data`：本地数据库、DAO、仓库
- `app/src/main/java/com/example/assetstar/domain`：领域模型、统计计算、用例
- `app/src/main/java/com/example/assetstar/ui`：页面、导航和 Compose 组件
- `app/src/main/java/com/example/assetstar/ui/voyage`：星舰记账与预算能源页面
- `app/src/main/java/com/example/assetstar/util`：图片、日期、金额格式化和视觉规则工具
- `app/src/main/res`：图标、图片、主题和 Android 资源

## 构建运行

```bash
./gradlew.bat :app:assembleDebug
```

Debug APK 输出位置：

```text
app/build/outputs/apk/debug/app-debug.apk
```

## 版本信息

- `versionCode`: 3
- `versionName`: 3.0.0
- 当前发布目标：`main`
