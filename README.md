# 资产星图

资产星图是一款使用 Kotlin 与 Jetpack Compose 开发的个人资产管理 App。它以“星图”作为首页视觉，将个人物品、分类、价值和持有成本以更直观的方式呈现。

## 主要功能

- 资产新增、编辑、删除
- 资产图片本地持久保存
- 首页资产星图总览
- 物品总值与持有日均成本统计
- 分类节点资产统计与跳转
- 明细页搜索、筛选与排序
- 资产详情页查看完整信息
- 分析页分类占比、状态分布、排行和年度购入分布
- 分类名称和图标管理
- 人民币/美元金额显示切换
- 实时汇率刷新
- 星空动态视觉效果和自定义底部导航

## 技术栈

- Kotlin
- Jetpack Compose
- Material 3
- Room
- Coroutines / Flow
- Coil

## 项目结构

- `app/src/main/java/com/example/assetstar/data`：本地数据库与仓库
- `app/src/main/java/com/example/assetstar/domain`：领域模型、计算逻辑和用例
- `app/src/main/java/com/example/assetstar/ui`：页面、导航和 Compose 组件
- `app/src/main/res`：图标、图片和 Android 资源

## 构建

```bash
./gradlew :app:assembleDebug
```

