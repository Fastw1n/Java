"use strict"

const vars = ["x", "y", "z"];

const abstractOperation = operation => (...operands) => (x, y, z) => {
    let result = [];
    operands.map(operand =>
        result.push(operand(x, y, z))
    );
    return operation(...result);
}

const cnst = function(a) {
    return function (x, y, z) {
        return a;
    }
}

const variable = function (x) {
    return function (...argument) {
        return argument[vars.indexOf(x)];
    }
}

const add = abstractOperation(function (x, y) {
    return x + y;
});

const subtract = abstractOperation(function (x, y) {
    return x - y;
});

const divide = abstractOperation(function (x, y) {
    return x / y;
});

const multiply = abstractOperation(function (x, y) {
    return x * y;
})

const negate = abstractOperation(function (x) {
    return -x;
});

const pi = cnst(Math.PI);
const e = cnst(Math.E);