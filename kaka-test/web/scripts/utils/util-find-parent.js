/**
 * 向上寻找父对象
 * @param {HTMLElement} element
 * @param {String} param 包含.表示寻找class的父级，包含#表示寻找id的父级
 * @param {Number} depth 向上查询父级的层级（深度）
 * @returns {element.parentNode}
 */
function findParent(element, param, depth) {
    var ps = param.split(" ");
    var pobjs = new Array();
    for (var n = 0; n < ps.length; n++) {
        var str = ps[n];
        var index = str.indexOf(".");
        if (index >= 0) {
            pobjs.push({type: 1, name: str.substr(1, str.length - 1)});
        } else {
            index = str.indexOf("#");
            if (index >= 0) {
                pobjs.push({type: 2, name: str.substr(1, str.length - 1)});
            } else {
                pobjs.push({type: 3, name: str});
            }
        }
    }
    if (pobjs.length == 0)
        return null;
    var max = (depth == undefined || depth <= 0) ? 10 : depth;
    var i = 0;
    while (i < max) {
        if (element == null || element == undefined) {
            break;
        }
        for (var j = 0; j < pobjs.length; j++) {
            var obj = pobjs[j];
            if (obj.type == 1) {
                if (element.className == obj.name) {
                    return element;
                }
            } else if (obj.type == 2) {
                if (element.id == obj.name) {
                    return element;
                }
            } else if (obj.type == 3) {
                if (element.name == obj.name) {
                    return element;
                }
            }
        }
        element = element.parentNode;
        i++;
    }
    return null;
}