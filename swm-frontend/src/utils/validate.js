export function isValidPlateNumber(plate) {
  return /^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤川青藏琼宁][A-Z][A-HJ-NP-Z0-9]{4,5}[A-HJ-NP-Z0-9挂学警港澳]$/.test(plate)
}

export function isValidPhone(phone) {
  return /^1[3-9]\d{9}$/.test(phone)
}

export function isPositiveNumber(val) {
  return val > 0
}
