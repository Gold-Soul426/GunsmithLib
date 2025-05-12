## 扩展功能通用格式:
在你的data json开头（大括号内）添加如下键值对：
```json5
{
  "gunsmithlib_extension": {
  },
}
```
此时你的data文件应该看起来像这样：
```json5
{
  // ......
  "gunsmithlib_extension": {
  },
  "ammo": "namespace:path",
  "ammo_amount": 666,
  "extended_mag_ammo_amount": [
    667,
    668,
    669
  ],
  "bolt": "open_bolt",
  // ......
}
```
## 开启武器充电功能:
在`gunsmithlib_extension`对象中加入如下键值对：
```json5
"battery": {
      // 每次射击消耗的电量，单位为FE
      "energy_per_shot": 200,
      // 充电功率，单位为FE/t
      "charge_power": 600,
      // 完全过热后是否需要装弹来解除过热锁定，就像地狱潜兵2的镰刀那样
      "needs_reload_on_full_heat": true
},
// 开启过热的视听反馈。开启后，过热时会播放音效并让武器冒烟。对所有能过热的武器都生效，而不仅仅是充电武器。
"enable_overheat_feedback": true
```
完整的data文件应该看起来像这样：
```json5
{
  // ......
  "gunsmithlib_extension": {
    "battery": {
      "energy_per_shot": 200,
      "charge_power": 600,
      "needs_reload_on_full_heat": true
    },
    "enable_overheat_feedback": true
  },
  "ammo": "namespace:path",
  "ammo_amount": 666,
  // ......
}
```