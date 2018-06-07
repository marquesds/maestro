# Jobs Queue APIs Overview

### Get jobs queue

Get current jobs queue state

**URL**: `/api/v1/jobs-queue`

**Method**: `GET`

#### Success Response

**Code**: `200 OK`

**Output Example**:

```json
{
    "waiting": [
        {
            "id": "f26e890b-df8e-422e-a39c-7762aa0bac36",
            "type": "rewards-questions",
            "urgent": true
        }
    ],
    "on_progress": [
        {
            "id": "ed0e23ef-6c2b-430c-9b90-cd4f1ff74c88",
            "type": "bills-questions",
            "urgent": false
        }
    ],
    "finished": []
}
```

#### Not Found Response

**Code**: `200 OK`

**Output Example**:

```json
{
    "waiting": [],
    "on_progress": [],
    "finished": []
}
```
