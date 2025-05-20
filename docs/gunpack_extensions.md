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
## 枪盾:
```json5
{
  // ......
  "gunsmithlib_extension": {
    "shield": {
      // 格挡原版伤害的角范围
      "block_vanilla_damage_angle": 60,
      // 格挡子弹的角范围
      "block_bullet_damage_angle": 120,
      // 格挡条件：
      // when_aiming      瞄准时能够格挡
      // when_not_aiming  没有瞄准时能够格挡
      // always           总是能够格挡，无论是否处于瞄准状态
      "condition": "when_aiming",
      // 换弹时是否停用格挡。优先级高于 condition
      "disable_shield_when_reloading": true
    }
  },
  // ......
}
```
## 火控（自瞄/制导）:
```json5
{
  "gunsmithlib_extension": {
    "fire_control": {
      // 能够锁定目标的角范围，单位为角度（°）
      "angular_range": 120,
      // （可选）指定最大锁定距离，如留空则使用武器的优势射程作为最大锁定距离。
      "range_override" : 500,
      // （可选）制导系统的扭矩，单位为°/tick。
      // 若留空，则子弹将在发射时直接指向目标。适用于高速动能子弹。
      // 填入的值代表弹头每tick将速度矢量转向目标方向的最大角度变化量。
      "torque": 6
    }
  },
}
```
注1：在填入torque（即使用制导模式）的情况下，武器的弹速不宜超过20，否则表现出来会比较奇怪。
<br><br>
注2：该系统已自带TaCZ Fire Control Extension的功能（可读取使用了该模组的制导武器的配置），那个模组可以扔掉了）