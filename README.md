# 资产星图 V2.0.0

当前分支：`develop/v2.0.0`

资产星图是一款使用 Kotlin 与 Jetpack Compose 开发的个人资产管理 App。它以“星图 / 星系 / 小行星节点”作为核心交互，把个人物品、分类、价值、持有成本和资产状态用更直观的视觉方式呈现。

## V2.0.0 重点

- 首页星图视觉升级：星空背景、中心物品总值星球、分类小行星节点、自定义底部导航。
- 人机交互增强：中心提示框、点击反馈、首页入场动效、轻量星图动效。
- 视觉反馈型游戏化：资产星等、资料完成度、高价值资产强调、珍藏标识。
- 分析页升级：分类占比、状态分布、持有成本排行、高价值排行、年度购入分布。
- 新增/编辑页风格统一：资产建档式表单、图片区域、分类图标选择、状态分段选择。
- 分类管理：支持修改分类显示名和分类图标。
- 汇率显示：支持人民币 / 美元显示切换，并可刷新实时汇率。

## 已有功能

- 资产新增、编辑、删除
- 资产图片本地持久保存
- 首页资产星图总览
- 物品总值与持有日均成本统计
- 分类节点统计与分类详情跳转
- 资产明细搜索、筛选与排序
- 资产详情页查看完整信息
- 珍藏资产标识
- 资产资料完成度
- 高价值资产视觉强调
- 分析页多维统计
- 分类名称和图标管理
- 人民币 / 美元金额显示切换
- 实时汇率刷新
- 自定义 App 桌面图标

## 技术栈

- Kotlin
- Jetpack Compose
- Material 3
- Room
- Coroutines / Flow
- Coil
- Canvas 自定义绘制

## 项目结构

- `app/src/main/java/com/example/assetstar/data`：本地数据库、DAO、仓库
- `app/src/main/java/com/example/assetstar/domain`：领域模型、统计计算、用例
- `app/src/main/java/com/example/assetstar/ui`：页面、导航和 Compose 组件
- `app/src/main/java/com/example/assetstar/util`：图片、日期、金额格式化和视觉规则工具
- `app/src/main/res`：图标、图片、主题和 Android 资源
- `Icon_Png`：设计图标源文件备份

## 构建运行

```bash
./gradlew.bat :app:assembleDebug
```

Debug APK 输出位置：

```text
app/build/outputs/apk/debug/app-debug.apk
```

## 版本信息

- `versionCode`: 2
- `versionName`: 2.0.0
- 目标分支：`develop/v2.0.0`
