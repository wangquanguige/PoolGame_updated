# 桌球游戏

## 注

本实现中：

1. 配置文件中的摩擦力值是一个乘法因子。该值越小，摩擦力越小。过高的值会让球无法移动。有效范围： `0 < friction < 1`。
2. 尽管击球时，拖动显示的线可以任意长，实际作用的力有个最大值。
3. 球的中心必须进入球袋的范围，而不仅是它们的矩形边界接触，才认为球入袋。

## 命令说明

- 运行 `gradle run` 将使用 `resources` 文件夹中的默认配置文件。使用 `gradle run --args="${CONFIG_PATH}"` 来加载自定义配置文件。
- 运行 `gradle javadoc` 可以生成自动文档。


