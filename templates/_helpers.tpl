{{/*
Expand the name of the chart.
*/}}
{{- define "mvn-stack.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" -}}
{{- end }}

{{/*
Create a default fully qualified app name.
*/}}
{{- define "mvn-stack.fullname" -}}
{{- if .Values.fullnameOverride }}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" -}}
{{- else }}
{{- $name := default .Chart.Name .Values.nameOverride }}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" -}}
{{- end }}
{{- end }}

{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "mvn-stack.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" -}}
{{- end }}

{{/*
Common labels
*/}}
{{- define "mvn-stack.labels" -}}
helm.sh/chart: {{ include "mvn-stack.chart" . }}
app.kubernetes.io/name: {{ include "mvn-stack.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
app.kubernetes.io/version: {{ .Chart.AppVersion }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}

{{/*
Selector labels (used for matchLabels)
*/}}
{{- define "mvn-stack.selectorLabels" -}}
app.kubernetes.io/name: {{ include "mvn-stack.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}

{{/*
Generate fullname for MySQL resources
*/}}
{{- define "mvn-stack.mysqlFullname" -}}
{{- printf "%s-mysql" (include "mvn-stack.fullname" .) | trunc 63 | trimSuffix "-" -}}
{{- end }}

{{/*
Generate fullname for Spring Boot backend resources
*/}}
{{- define "mvn-stack.backendFullname" -}}
{{- printf "%s-backend" (include "mvn-stack.fullname" .) | trunc 63 | trimSuffix "-" -}}
{{- end }}

{{/*
Generate fullname for Angular frontend resources
*/}}
{{- define "mvn-stack.frontendFullname" -}}
{{- printf "%s-frontend" (include "mvn-stack.fullname" .) | trunc 63 | trimSuffix "-" -}}
{{- end }}
