import js from '@eslint/js'
import pluginVue from 'eslint-plugin-vue'
import globals from 'globals'

export default [
  { ignores: ['dist/**', 'coverage/**'] },
  js.configs.recommended,
  ...pluginVue.configs['flat/essential'],
  { languageOptions: { globals: globals.browser } },
  { files: ['vite.config.js', 'eslint.config.js'], languageOptions: { globals: globals.node } }
]
