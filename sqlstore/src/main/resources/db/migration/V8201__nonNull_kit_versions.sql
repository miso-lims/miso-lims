-- fix old kits for testing
UPDATE KitDescriptor SET version = 1 WHERE version IS NULL;